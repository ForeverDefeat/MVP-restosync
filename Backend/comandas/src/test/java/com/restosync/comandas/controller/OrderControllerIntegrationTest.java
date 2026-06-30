package com.restosync.comandas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restosync.comandas.IntegrationTestSupport;
import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rolNoAutorizadoDevuelve403() throws Exception {
        mockMvc.perform(get("/api/orders/active/cocina")
                        .header("Authorization", bearer(waiter)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("ACCESS_DENIED"));
    }

    @Test
    void meseroCreaPedidoConProductoDisponible() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestDataFactory.createOrderRequest(plate.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDIENTE"))
                .andExpect(jsonPath("$.data.items[0].productName").value(plate.getName()));
    }

    @Test
    void productoNoDisponibleDevuelve409() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.createOrderRequest(unavailablePlate.getId()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("BUSINESS_CONFLICT"));
    }

    @Test
    void cicloDeVidaValidoAvanzaHastaEntregado() throws Exception {
        Order order = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate));

        mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                        .header("Authorization", bearer(cook))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.statusRequest(OrderStatus.EN_PREPARACION))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("EN_PREPARACION"));

        mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                        .header("Authorization", bearer(cook))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.statusRequest(OrderStatus.LISTO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("LISTO"));

        mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.statusRequest(OrderStatus.ENTREGADO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ENTREGADO"));
    }

    @Test
    void transicionInvalidaDevuelve422() throws Exception {
        Order order = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate));

        mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.statusRequest(OrderStatus.LISTO))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_STATE_TRANSITION"));
    }

    @Test
    void bartenderNoPuedeAvanzarPedidoDePlatos() throws Exception {
        Order order = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate));

        mockMvc.perform(patch("/api/orders/{id}/status", order.getId())
                        .header("Authorization", bearer(bartender))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.statusRequest(OrderStatus.EN_PREPARACION))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED_ROLE"));
    }

    @Test
    void cancelacionYEdicionSoloEnPendiente() throws Exception {
        Order pending = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.PENDIENTE, plate));
        Order inProgress = orderRepository.save(TestDataFactory.order(waiter, OrderStatus.EN_PREPARACION, plate));

        mockMvc.perform(patch("/api/orders/{id}/items", pending.getId())
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestDataFactory.editOrderItemsRequest(drink.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].productName").value(drink.getName()));

        mockMvc.perform(delete("/api/orders/{id}", inProgress.getId())
                        .header("Authorization", bearer(waiter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestDataFactory.cancelRequest("Cliente se retiro"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("BUSINESS_CONFLICT"));
    }
}
