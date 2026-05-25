package com.restosync.comandas.mapper;

import com.restosync.comandas.dto.request.CreateProductRequest;
import com.restosync.comandas.dto.response.ProductResponse;
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
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.available( product.getAvailable() );
        productResponse.category( product.getCategory() );
        productResponse.createdAt( product.getCreatedAt() );
        productResponse.estimatedMinutes( product.getEstimatedMinutes() );
        productResponse.id( product.getId() );
        productResponse.imageUrl( product.getImageUrl() );
        productResponse.name( product.getName() );
        productResponse.price( product.getPrice() );
        productResponse.updatedAt( product.getUpdatedAt() );

        return productResponse.build();
    }

    @Override
    public List<ProductResponse> toResponseList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductResponse> list = new ArrayList<ProductResponse>( products.size() );
        for ( Product product : products ) {
            list.add( toResponse( product ) );
        }

        return list;
    }

    @Override
    public Product toEntity(CreateProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.available( request.getAvailable() );
        product.category( request.getCategory() );
        product.estimatedMinutes( request.getEstimatedMinutes() );
        product.imageUrl( request.getImageUrl() );
        product.name( request.getName() );
        product.price( request.getPrice() );

        return product.build();
    }

    @Override
    public void updateEntity(CreateProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setAvailable( request.getAvailable() );
        product.setCategory( request.getCategory() );
        product.setEstimatedMinutes( request.getEstimatedMinutes() );
        product.setImageUrl( request.getImageUrl() );
        product.setName( request.getName() );
        product.setPrice( request.getPrice() );
    }
}
