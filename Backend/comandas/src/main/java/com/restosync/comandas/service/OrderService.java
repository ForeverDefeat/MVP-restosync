package com.restosync.comandas.service;

import com.restosync.comandas.dto.request.CancelOrderRequest;
import com.restosync.comandas.dto.request.CreateOrderRequest;
import com.restosync.comandas.dto.request.EditOrderItemsRequest;
import com.restosync.comandas.dto.request.UpdateOrderStatusRequest;
import com.restosync.comandas.dto.response.OrderResponse;
import com.restosync.comandas.dto.response.PagedResponse;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.entity.OrderItem;
import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.*;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.exception.InvalidStateTransitionException;
import com.restosync.comandas.exception.ResourceNotFoundException;
import com.restosync.comandas.exception.UnauthorizedRoleException;
import com.restosync.comandas.mapper.OrderMapper;
import com.restosync.comandas.repository.OrderRepository;
import com.restosync.comandas.repository.ProductRepository;
import com.restosync.comandas.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.util.concurrent.atomic.AtomicInteger;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
 
    private final OrderRepository       orderRepository;
    private final ProductRepository     productRepository;
    private final OrderMapper           orderMapper;
    private final AuditService          auditService;
    private final NotificationService   notificationService;
 
    // ── Máquina de estados ───────────────────────────────────────────────────
 
    /**
     * Define las transiciones válidas del sistema.
     * Cualquier otra combinación lanzará InvalidStateTransitionException.
     */
    private static final Map<OrderStatus, OrderStatus> TRANSICIONES_VALIDAS = Map.of(
            OrderStatus.PENDIENTE,       OrderStatus.EN_PREPARACION,
            OrderStatus.EN_PREPARACION,  OrderStatus.LISTO,
            OrderStatus.LISTO,           OrderStatus.ENTREGADO
    );
 
    /**
     * Estados que permiten la transición a EN_PREPARACION según el rol.
     * Cocina mueve PLATOS, Bar mueve BEBIDAS.
     */
    private static final Map<UserRole, ProductCategory> ROL_CATEGORIA = Map.of(
            UserRole.COCINERO,    ProductCategory.PLATO,
            UserRole.BARTENDER,   ProductCategory.BEBIDA
    );
 
    // ── Consultas ────────────────────────────────────────────────────────────
 
    @Transactional(readOnly = true)
    public List<OrderResponse> obtenerActivosPorCategoria(ProductCategory categoria) {
        List<OrderStatus> estadosActivos = List.of(
                OrderStatus.PENDIENTE,
                OrderStatus.EN_PREPARACION,
                OrderStatus.LISTO
        );
        return orderMapper.toResponseList(
                orderRepository.findActiveByItemCategory(estadosActivos, categoria)
        );
    }
 
    @Transactional(readOnly = true)
    public List<OrderResponse> obtenerActivosPorMesero(Long waiterId) {
        return orderMapper.toResponseList(
                orderRepository.findActiveByWaiter(waiterId)
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> obtenerActivos() {
        return orderMapper.toResponseList(orderRepository.findActive());
    }
 
    @Transactional(readOnly = true)
    public OrderResponse buscarPorId(Long id) {
        return orderMapper.toResponse(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public OrderResponse buscarPorId(Long id, User currentUser) {
        Order order = obtenerEntidad(id);
        validarAccesoLectura(currentUser, order);
        return orderMapper.toResponse(order);
    }
 
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> obtenerHistorial(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String        tableOrRegister,
            Long          waiterId,
            OrderStatus   status,
            int           page,
            int           size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> resultado = orderRepository.findHistory(
                startDate, endDate, tableOrRegister, waiterId, status, pageable
        );
        Page<OrderResponse> mapped = resultado.map(orderMapper::toResponse);
        return PagedResponse.from(mapped);
    }
 
    // ── Creación ─────────────────────────────────────────────────────────────
 
    @Transactional
    public OrderResponse crear(CreateOrderRequest request, User waiter) {
 
        // 1. Construir la entidad Order vacía
        Order order = Order.builder()
                .ticketNumber(generarTicketNumber())
                .tableOrRegister(TextNormalizer.required(request.getTableOrRegister()))
                .status(OrderStatus.PENDIENTE)
                .waiter(waiter)
                .build();
 
        // 2. Crear y adjuntar cada ítem
        for (var itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto", itemReq.getProductId()
                    ));
 
            if (!product.getAvailable()) {
                throw new BusinessException(
                        "El producto '" + product.getName() + "' no está disponible"
                );
            }
 
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())     // snapshot
                    .category(product.getCategory())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .notes(TextNormalizer.nullable(itemReq.getNotes()))
                    .build();
 
            order.addItem(item);
        }
 
        // 3. Calcular total y persistir
        order.recalculateTotal();
        order = orderRepository.save(order);
 
        OrderResponse response = orderMapper.toResponse(order);
 
        // 4. Notificar en tiempo real a los paneles correspondientes
        notificationService.notificarSegunCategoria(response);
        notificationService.notificarMesero(waiter.getId(), response);
 
        // 5. Audit log
        auditService.log(
                AuditService.ORDER_CREATED,
                waiter.getId(),
                order.getId(),
                Map.of(
                        "ticketNumber",   order.getTicketNumber(),
                        "table",          order.getTableOrRegister(),
                        "totalItems",     order.getItems().size(),
                        "total",          order.getTotal().toString()
                )
        );
 
        log.info("Comanda creada: ticket={} mesa='{}' mesero='{}'",
                order.getTicketNumber(), order.getTableOrRegister(), waiter.getName());
 
        return response;
    }
 
    // ── Cambiar estado ───────────────────────────────────────────────────────
 
    @Transactional
    public OrderResponse cambiarEstado(
            Long id,
            UpdateOrderStatusRequest request,
            User currentUser
    ) {
        Order       order     = obtenerEntidad(id);
        OrderStatus estadoAct = order.getStatus();
        OrderStatus estadoNvo = request.getNewStatus();
 
        // 1. Validar flujo secuencial
        validarTransicion(estadoAct, estadoNvo);
 
        // 2. Validar permisos por rol y categoría
        validarPermisoPorRol(currentUser.getRole(), estadoAct, estadoNvo, order);
        validarPropiedadMesero(currentUser, order, "cambiar el estado de pedidos de otros meseros");
 
        // 3. Aplicar cambio
        order.setStatus(estadoNvo);
        order = orderRepository.save(order);
 
        OrderResponse response = orderMapper.toResponse(order);
 
        // 4. Notificar
        notificationService.notificarSegunCategoria(response);
        notificationService.notificarMesero(order.getWaiter().getId(), response);
 
        // 5. Audit log
        auditService.log(
                AuditService.ORDER_STATUS_CHANGED,
                currentUser.getId(),
                order.getId(),
                Map.of(
                        "ticketNumber", order.getTicketNumber(),
                        "from",         estadoAct.name(),
                        "to",           estadoNvo.name()
                )
        );
 
        log.info("Comanda {}: estado {} → {}", order.getTicketNumber(), estadoAct, estadoNvo);
        return response;
    }
 
    // ── Cancelar ─────────────────────────────────────────────────────────────
 
    @Transactional
    public OrderResponse cancelar(Long id, CancelOrderRequest request, User currentUser) {
        Order order = obtenerEntidad(id);
        validarPropiedadMesero(currentUser, order, "cancelar pedidos de otros meseros");
 
        // Solo se puede cancelar en estado PENDIENTE
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new BusinessException(
                    "No se puede cancelar la comanda " + order.getTicketNumber() +
                    ". Solo es posible cancelar pedidos en estado PENDIENTE. " +
                    "Estado actual: " + order.getStatus()
            );
        }
 
        order.setStatus(OrderStatus.CANCELADO);
        order.setCancellationReason(TextNormalizer.required(request.getReason()));
        order = orderRepository.save(order);
 
        OrderResponse response = orderMapper.toResponse(order);
        notificationService.notificarMesero(order.getWaiter().getId(), response);
        notificationService.notificarSegunCategoria(response);
 
        auditService.log(
                AuditService.ORDER_CANCELLED,
                currentUser.getId(),
                order.getId(),
                Map.of(
                        "ticketNumber", order.getTicketNumber(),
                        "reason",       request.getReason()
                )
        );
 
        log.info("Comanda {} cancelada. Motivo: '{}'",
                order.getTicketNumber(), request.getReason());
        return response;
    }
 
    // ── Editar ítems ─────────────────────────────────────────────────────────
 
    @Transactional
    public OrderResponse editarItems(Long id, EditOrderItemsRequest request, User currentUser) {
        Order order = obtenerEntidad(id);
        validarPropiedadMesero(currentUser, order, "editar pedidos de otros meseros");
 
        // Solo se puede editar en PENDIENTE
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new BusinessException(
                    "No se pueden editar los ítems de la comanda " + order.getTicketNumber() +
                    ". Solo es posible editar pedidos en estado PENDIENTE. " +
                    "Estado actual: " + order.getStatus()
            );
        }
 
        // Limpiar ítems actuales y reemplazar
        order.getItems().clear();
 
        for (var itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto", itemReq.getProductId()
                    ));
 
            if (!product.getAvailable()) {
                throw new BusinessException(
                        "El producto '" + product.getName() + "' no está disponible"
                );
            }
 
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .category(product.getCategory())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .notes(TextNormalizer.nullable(itemReq.getNotes()))
                    .build();
 
            order.addItem(item);
        }
 
        order.recalculateTotal();
        order = orderRepository.save(order);
 
        OrderResponse response = orderMapper.toResponse(order);
        notificationService.notificarSegunCategoria(response);
 
        auditService.log(
                AuditService.ORDER_ITEMS_EDITED,
                currentUser.getId(),
                order.getId(),
                Map.of(
                        "ticketNumber", order.getTicketNumber(),
                        "newTotal",     order.getTotal().toString(),
                        "itemCount",    order.getItems().size()
                )
        );
 
        return response;
    }
 
    // ── Validaciones internas ────────────────────────────────────────────────
 
    /**
     * Verifica que la transición de estado sea parte del flujo válido:
     * PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO
     */
    private void validarTransicion(OrderStatus desde, OrderStatus hasta) {
        OrderStatus siguiente = TRANSICIONES_VALIDAS.get(desde);
        if (siguiente == null || siguiente != hasta) {
            throw new InvalidStateTransitionException(desde, hasta);
        }
    }
 
    /**
     * Verifica que el rol del usuario tenga permiso para el cambio de estado
     * considerando la categoría de los ítems del pedido.
     *
     * Reglas:
     *  - PENDIENTE → EN_PREPARACION: COCINERO si hay PLATOs, BARTENDER si hay BEBIDAs.
     *  - EN_PREPARACION → LISTO: mismo rol que inició (mismo criterio de categoría).
     *  - LISTO → ENTREGADO: solo MESERO.
     *  - ADMINISTRADOR: puede realizar cualquier transición.
     */
    private void validarPermisoPorRol(
            UserRole    rol,
            OrderStatus desde,
            OrderStatus hasta,
            Order       order
    ) {
        // Admin tiene permisos totales
        if (rol == UserRole.ADMINISTRADOR) return;
 
        // LISTO → ENTREGADO: solo el mesero puede marcar como entregado
        if (desde == OrderStatus.LISTO && hasta == OrderStatus.ENTREGADO) {
            if (rol != UserRole.MESERO) {
                throw new UnauthorizedRoleException(rol, "marcar pedido como ENTREGADO");
            }
            return;
        }
 
        // PENDIENTE → EN_PREPARACION  y  EN_PREPARACION → LISTO
        if (desde == OrderStatus.PENDIENTE && hasta == OrderStatus.EN_PREPARACION ||
            desde == OrderStatus.EN_PREPARACION && hasta == OrderStatus.LISTO) {
 
            ProductCategory categoriaRequerida = ROL_CATEGORIA.get(rol);
            if (categoriaRequerida == null) {
                // El rol no es COCINERO ni BARTENDER (p.ej. MESERO intentando iniciar)
                throw new UnauthorizedRoleException(rol,
                        "cambiar estado de " + desde + " a " + hasta);
            }
 
            boolean tieneItemsDeCategoria = order.getItems().stream()
                    .anyMatch(i -> i.getCategory() == categoriaRequerida);
 
            if (!tieneItemsDeCategoria) {
                throw new UnauthorizedRoleException(rol,
                        "El pedido no contiene ítems de categoría " + categoriaRequerida);
            }
        }
    }
 
    // ── Utilitarios ──────────────────────────────────────────────────────────
 
    private void validarAccesoLectura(User currentUser, Order order) {
        if (currentUser.getRole() == UserRole.ADMINISTRADOR) return;
        if (currentUser.getRole() == UserRole.MESERO && order.getWaiter().getId().equals(currentUser.getId())) return;
        if (currentUser.getRole() == UserRole.COCINERO && contieneCategoria(order, ProductCategory.PLATO)) return;
        if (currentUser.getRole() == UserRole.BARTENDER && contieneCategoria(order, ProductCategory.BEBIDA)) return;

        throw new UnauthorizedRoleException(currentUser.getRole(), "consultar este pedido");
    }

    private void validarPropiedadMesero(User currentUser, Order order, String accion) {
        if (currentUser.getRole() != UserRole.MESERO) return;
        if (!order.getWaiter().getId().equals(currentUser.getId())) {
            throw new UnauthorizedRoleException(currentUser.getRole(), accion);
        }
    }

    private boolean contieneCategoria(Order order, ProductCategory categoria) {
        return order.getItems().stream().anyMatch(item -> item.getCategory() == categoria);
    }

    private Order obtenerEntidad(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda", id));
    }
 
    /**
     * Genera un número de ticket único con formato #XXXX.
     * En producción se podría usar una secuencia de BD para mayor robustez.
     */
    private static final AtomicInteger TICKET_COUNTER = new AtomicInteger(1000);
 
    private String generarTicketNumber() {
        String candidate;
        do {
            candidate = "#" + TICKET_COUNTER.incrementAndGet();
        } while (orderRepository.existsByTicketNumber(candidate));
        return candidate;
    }
}
