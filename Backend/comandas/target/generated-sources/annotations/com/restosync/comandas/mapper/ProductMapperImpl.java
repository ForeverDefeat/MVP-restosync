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
    date = "2026-06-29T18:51:10-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.id( product.getId() );
        productResponse.name( product.getName() );
        productResponse.category( product.getCategory() );
        productResponse.price( product.getPrice() );
        productResponse.available( product.getAvailable() );
        productResponse.estimatedMinutes( product.getEstimatedMinutes() );
        productResponse.imageUrl( product.getImageUrl() );
        productResponse.createdAt( product.getCreatedAt() );
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

        product.name( request.getName() );
        product.category( request.getCategory() );
        product.price( request.getPrice() );
        product.available( request.getAvailable() );
        product.estimatedMinutes( request.getEstimatedMinutes() );
        product.imageUrl( request.getImageUrl() );

        return product.build();
    }

    @Override
    public void updateEntity(CreateProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setName( request.getName() );
        product.setCategory( request.getCategory() );
        product.setPrice( request.getPrice() );
        product.setAvailable( request.getAvailable() );
        product.setEstimatedMinutes( request.getEstimatedMinutes() );
        product.setImageUrl( request.getImageUrl() );
    }
}
