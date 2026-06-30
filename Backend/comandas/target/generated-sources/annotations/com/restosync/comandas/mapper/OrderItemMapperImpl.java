package com.restosync.comandas.mapper;

import com.restosync.comandas.dto.response.OrderItemResponse;
import com.restosync.comandas.entity.OrderItem;
import com.restosync.comandas.entity.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T18:51:10-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItemResponse toResponse(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.productId( itemProductId( item ) );
        orderItemResponse.id( item.getId() );
        orderItemResponse.productName( item.getProductName() );
        orderItemResponse.category( item.getCategory() );
        orderItemResponse.quantity( item.getQuantity() );
        orderItemResponse.unitPrice( item.getUnitPrice() );
        orderItemResponse.notes( item.getNotes() );

        orderItemResponse.subtotal( item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())) );

        return orderItemResponse.build();
    }

    @Override
    public List<OrderItemResponse> toResponseList(List<OrderItem> items) {
        if ( items == null ) {
            return null;
        }

        List<OrderItemResponse> list = new ArrayList<OrderItemResponse>( items.size() );
        for ( OrderItem orderItem : items ) {
            list.add( toResponse( orderItem ) );
        }

        return list;
    }

    private Long itemProductId(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getId();
    }
}
