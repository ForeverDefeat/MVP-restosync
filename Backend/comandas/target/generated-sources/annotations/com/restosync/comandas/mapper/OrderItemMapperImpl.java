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
    date = "2026-05-25T13:59:37-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        orderItemResponse.category( item.getCategory() );
        orderItemResponse.id( item.getId() );
        orderItemResponse.notes( item.getNotes() );
        orderItemResponse.productName( item.getProductName() );
        orderItemResponse.quantity( item.getQuantity() );
        orderItemResponse.unitPrice( item.getUnitPrice() );

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
