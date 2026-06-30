package com.restosync.comandas;

import com.restosync.comandas.dto.request.CancelOrderRequest;
import com.restosync.comandas.dto.request.CreateOrderRequest;
import com.restosync.comandas.dto.request.EditOrderItemsRequest;
import com.restosync.comandas.dto.request.LoginRequest;
import com.restosync.comandas.dto.request.OrderItemRequest;
import com.restosync.comandas.dto.request.UpdateOrderStatusRequest;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.entity.OrderItem;
import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.OrderStatus;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User user(String email, UserRole role, String encodedPassword) {
        return User.builder()
                .name(role.name() + " Test")
                .email(email)
                .password(encodedPassword)
                .role(role)
                .active(true)
                .build();
    }

    public static Product product(String name, ProductCategory category, boolean available) {
        return Product.builder()
                .name(name)
                .category(category)
                .price(new BigDecimal("25.50"))
                .available(available)
                .estimatedMinutes(12)
                .build();
    }

    public static Order order(User waiter, OrderStatus status, Product... products) {
        Order order = Order.builder()
                .ticketNumber("#T" + System.nanoTime())
                .tableOrRegister("Mesa 7")
                .status(status)
                .waiter(waiter)
                .build();

        for (Product product : products) {
            order.addItem(OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .category(product.getCategory())
                    .quantity(1)
                    .unitPrice(product.getPrice())
                    .build());
        }

        order.recalculateTotal();
        return order;
    }

    public static LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    public static CreateOrderRequest createOrderRequest(Long productId) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTableOrRegister("Mesa 3");
        request.setItems(List.of(orderItemRequest(productId, 2)));
        return request;
    }

    public static EditOrderItemsRequest editOrderItemsRequest(Long productId) {
        EditOrderItemsRequest request = new EditOrderItemsRequest();
        request.setItems(List.of(orderItemRequest(productId, 1)));
        return request;
    }

    public static OrderItemRequest orderItemRequest(Long productId, int quantity) {
        OrderItemRequest request = new OrderItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        request.setNotes("Sin cebolla");
        return request;
    }

    public static UpdateOrderStatusRequest statusRequest(OrderStatus status) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setNewStatus(status);
        return request;
    }

    public static CancelOrderRequest cancelRequest(String reason) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setReason(reason);
        return request;
    }
}
