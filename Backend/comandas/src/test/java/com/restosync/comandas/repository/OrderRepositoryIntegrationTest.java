package com.restosync.comandas.repository;

import com.restosync.comandas.IntegrationTestSupport;
import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.enums.OrderStatus;
import com.restosync.comandas.enums.ProductCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryIntegrationTest extends IntegrationTestSupport {

    @Test
    void findActiveByItemCategoryFiltraPorCategoriaYEstadosActivos() {
        Order plateOrder = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate));
        orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, drink));
        orderRepository.save(TestDataFactory.order(waiter, OrderStatus.ENTREGADO, plate));

        List<Order> result = orderRepository.findActiveByItemCategory(
                List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO),
                ProductCategory.PLATO);

        assertThat(result).extracting(Order::getId).containsExactly(plateOrder.getId());
    }
}
