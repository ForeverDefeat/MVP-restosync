package com.restosync.comandas.service;

import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.dto.response.OrderResponse;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.OrderStatus;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.exception.InvalidStateTransitionException;
import com.restosync.comandas.exception.UnauthorizedRoleException;
import com.restosync.comandas.mapper.OrderMapper;
import com.restosync.comandas.repository.OrderRepository;
import com.restosync.comandas.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    AuditService auditService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    OrderService orderService;

    @Test
    void meseroCreaPedidoConProductoDisponible() {
        User waiter = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        waiter.setId(1L);
        Product plate = TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true);
        plate.setId(5L);
        OrderResponse mapped = OrderResponse.builder().id(99L).status(OrderStatus.PENDIENTE).build();

        when(productRepository.findById(plate.getId())).thenReturn(Optional.of(plate));
        when(orderRepository.existsByTicketNumber(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mapped);

        OrderResponse result = orderService.crear(TestDataFactory.createOrderRequest(plate.getId()), waiter);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDIENTE);
        verify(notificationService).notificarSegunCategoria(mapped);
        verify(notificationService).notificarMesero(waiter.getId(), mapped);
        verify(auditService).log(any(), any(), any(), any());
    }

    @Test
    void productoNoDisponibleBloqueaCreacion() {
        User waiter = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        Product unavailable = TestDataFactory.product("Ceviche agotado", ProductCategory.PLATO, false);
        unavailable.setId(7L);
        when(productRepository.findById(unavailable.getId())).thenReturn(Optional.of(unavailable));

        assertThatThrownBy(() -> orderService.crear(TestDataFactory.createOrderRequest(unavailable.getId()), waiter))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no est");
    }

    @Test
    void cocineroPuedeAvanzarPedidoConPlatosAPreparacion() {
        User waiter = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        waiter.setId(1L);
        User cook = TestDataFactory.user("cocina@test.local", UserRole.COCINERO, "encoded");
        cook.setId(2L);
        Product plate = TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true);
        Order order = TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate);
        order.setId(3L);
        OrderResponse mapped = OrderResponse.builder().id(3L).status(OrderStatus.EN_PREPARACION).build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(mapped);

        OrderResponse result = orderService.cambiarEstado(
                order.getId(),
                TestDataFactory.statusRequest(OrderStatus.EN_PREPARACION),
                cook);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.EN_PREPARACION);
    }

    @Test
    void bartenderNoPuedeAvanzarPedidoSoloConPlatos() {
        User waiter = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        User bartender = TestDataFactory.user("bar@test.local", UserRole.BARTENDER, "encoded");
        Product plate = TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true);
        Order order = TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate);
        order.setId(3L);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cambiarEstado(
                order.getId(),
                TestDataFactory.statusRequest(OrderStatus.EN_PREPARACION),
                bartender))
                .isInstanceOf(UnauthorizedRoleException.class);
    }

    @Test
    void rechazaTransicionInvalida() {
        User waiter = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        Product plate = TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true);
        Order order = TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate);
        order.setId(3L);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cambiarEstado(
                order.getId(),
                TestDataFactory.statusRequest(OrderStatus.LISTO),
                waiter))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void meseroNoPuedeEntregarPedidoAjeno() {
        User owner = TestDataFactory.user("owner@test.local", UserRole.MESERO, "encoded");
        owner.setId(1L);
        User otherWaiter = TestDataFactory.user("other@test.local", UserRole.MESERO, "encoded");
        otherWaiter.setId(2L);
        Product plate = TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true);
        Order order = TestDataFactory.order(owner, OrderStatus.LISTO, plate);
        order.setId(3L);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cambiarEstado(
                order.getId(),
                TestDataFactory.statusRequest(OrderStatus.ENTREGADO),
                otherWaiter))
                .isInstanceOf(UnauthorizedRoleException.class);
    }
}
