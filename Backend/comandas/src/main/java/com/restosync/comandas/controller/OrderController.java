package com.restosync.comandas.controller;

import com.restosync.comandas.dto.request.CancelOrderRequest;
import com.restosync.comandas.dto.request.CreateOrderRequest;
import com.restosync.comandas.dto.request.EditOrderItemsRequest;
import com.restosync.comandas.dto.request.UpdateOrderStatusRequest;
import com.restosync.comandas.dto.response.ApiResponse;
import com.restosync.comandas.dto.response.OrderResponse;
import com.restosync.comandas.dto.response.PagedResponse;
import com.restosync.comandas.enums.OrderStatus;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.security.SecurityUser;
import com.restosync.comandas.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 
import java.time.LocalDateTime;
import java.util.List;
 
/**
 * Endpoints del ciclo de vida completo de pedidos.
 *
 * ── Creación y edición (MESERO) ────────────────────────────────────────────
 * POST   /api/orders                          → crear comanda
 * PATCH  /api/orders/{id}/items               → editar ítems (solo si PENDIENTE)
 * DELETE /api/orders/{id}                     → cancelar con motivo (solo si PENDIENTE)
 *
 * ── Cambio de estado (COCINERO / BARTENDER / MESERO) ──────────────────────
 * PATCH  /api/orders/{id}/status              → avanzar estado
 *
 * ── Consulta (todos los roles autenticados) ───────────────────────────────
 * GET    /api/orders/{id}                     → detalle de un pedido
 * GET    /api/orders/my                       → comandas activas del mesero autenticado
 * GET    /api/orders/active/cocina            → panel KDS — platos activos
 * GET    /api/orders/active/bar               → panel Bar — bebidas activas
 * GET    /api/orders/history                  → historial paginado con filtros
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pedidos (Comandas)", description = "Ciclo de vida completo de las comandas")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
 
    private final OrderService orderService;
 
    // ── Creación ──────────────────────────────────────────────────────────────
 
    @PostMapping
    @PreAuthorize("hasAnyRole('MESERO', 'ADMINISTRADOR')")
    @Operation(summary = "Crear nueva comanda",
               description = "El sistema calcula el total y enruta automáticamente a cocina y/o bar")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        OrderResponse order = orderService.crear(request, principal.getUser());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(order, "Comanda creada correctamente"));
    }
 
    // ── Cambio de estado ──────────────────────────────────────────────────────
 
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COCINERO', 'BARTENDER', 'MESERO', 'ADMINISTRADOR')")
    @Operation(summary = "Cambiar estado del pedido",
               description = "Flujo válido: PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO. " +
                             "La validación de rol por categoría se aplica en el servicio.")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable @Positive(message = "El ID del pedido debe ser mayor a cero") Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        OrderResponse order = orderService.cambiarEstado(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(order, "Estado actualizado correctamente"));
    }
 
    // ── Edición de ítems ──────────────────────────────────────────────────────
 
    @PatchMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMINISTRADOR')")
    @Operation(summary = "Editar ítems del pedido",
               description = "Solo disponible mientras el pedido esté en estado PENDIENTE")
    public ResponseEntity<ApiResponse<OrderResponse>> editItems(
            @PathVariable @Positive(message = "El ID del pedido debe ser mayor a cero") Long id,
            @Valid @RequestBody EditOrderItemsRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        OrderResponse order = orderService.editarItems(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(order, "Pedido actualizado correctamente"));
    }
 
    // ── Cancelación ───────────────────────────────────────────────────────────
 
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMINISTRADOR')")
    @Operation(summary = "Cancelar pedido",
               description = "Requiere motivo obligatorio. Solo disponible en estado PENDIENTE.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @PathVariable @Positive(message = "El ID del pedido debe ser mayor a cero") Long id,
            @Valid @RequestBody CancelOrderRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        OrderResponse order = orderService.cancelar(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(order, "Pedido cancelado correctamente"));
    }
 
    // ── Consultas de panel en tiempo real ─────────────────────────────────────
 
    @GetMapping("/active/cocina")
    @PreAuthorize("hasAnyRole('COCINERO', 'ADMINISTRADOR')")
    @Operation(summary = "Panel KDS — platos activos",
               description = "Pedidos en estado PENDIENTE o EN_PREPARACION con ítems de categoría PLATO")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getActiveCocina() {
        List<OrderResponse> orders = orderService.obtenerActivosPorCategoria(ProductCategory.PLATO);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }
 
    @GetMapping("/active/bar")
    @PreAuthorize("hasAnyRole('BARTENDER', 'ADMINISTRADOR')")
    @Operation(summary = "Panel Bar — bebidas activas",
               description = "Pedidos en estado PENDIENTE o EN_PREPARACION con ítems de categoría BEBIDA")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getActiveBar() {
        List<OrderResponse> orders = orderService.obtenerActivosPorCategoria(ProductCategory.BEBIDA);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }
 
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('MESERO', 'ADMINISTRADOR')")
    @Operation(summary = "Mis comandas activas",
               description = "Comandas del mesero autenticado que no están ENTREGADAS ni CANCELADAS")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMy(
            @AuthenticationPrincipal SecurityUser principal) {
 
        List<OrderResponse> orders = principal.getRole() == UserRole.ADMINISTRADOR
                ? orderService.obtenerActivos()
                : orderService.obtenerActivosPorMesero(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }
 
    // ── Detalle ───────────────────────────────────────────────────────────────
 
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @PathVariable @Positive(message = "El ID del pedido debe ser mayor a cero") Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        OrderResponse order = orderService.buscarPorId(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(order));
    }
 
    // ── Historial paginado con filtros ────────────────────────────────────────
 
    @GetMapping("/history")
    @Operation(summary = "Historial de pedidos",
               description = "Pedidos ENTREGADOS y CANCELADOS, filtrados y paginados. " +
                             "El mesero solo ve sus propios pedidos; el admin ve todos.")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getHistory(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
 
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
 
            @RequestParam(required = false) @Size(max = 50, message = "La mesa o caja no puede superar 50 caracteres")
            String tableOrRegister,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "La pagina no puede ser negativa") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "El tamano de pagina minimo es 1")
            @Max(value = 100, message = "El tamano de pagina maximo es 100") int size,
 
            @AuthenticationPrincipal SecurityUser principal) {
 
        // El mesero solo puede ver su propio historial
        Long waiterId = null;
        if (principal.getRole() == UserRole.MESERO) {
            waiterId = principal.getUserId();
        }
 
        PagedResponse<OrderResponse> history = orderService.obtenerHistorial(
                startDate, endDate, tableOrRegister, waiterId, status, page, size
        );
 
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
