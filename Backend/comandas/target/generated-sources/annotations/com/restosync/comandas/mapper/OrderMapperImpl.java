package com.restosync.comandas.mapper;

import com.restosync.comandas.dto.response.OrderResponse;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T13:59:37-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.waiterId( orderWaiterId( order ) );
        orderResponse.waiterName( orderWaiterName( order ) );
        orderResponse.cancellationReason( order.getCancellationReason() );
        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.id( order.getId() );
        orderResponse.items( orderItemMapper.toResponseList( order.getItems() ) );
        orderResponse.status( order.getStatus() );
        orderResponse.tableOrRegister( order.getTableOrRegister() );
        orderResponse.ticketNumber( order.getTicketNumber() );
        orderResponse.total( order.getTotal() );
        orderResponse.updatedAt( order.getUpdatedAt() );

        return orderResponse.build();
    }

    @Override
    public List<OrderResponse> toResponseList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderResponse> list = new ArrayList<OrderResponse>( orders.size() );
        for ( Order order : orders ) {
            list.add( toResponse( order ) );
        }

        return list;
    }

    private Long orderWaiterId(Order order) {
        User waiter = order.getWaiter();
        if ( waiter == null ) {
            return null;
        }
        return waiter.getId();
    }

    private String orderWaiterName(Order order) {
        User waiter = order.getWaiter();
        if ( waiter == null ) {
            return null;
        }
        return waiter.getName();
    }
}
