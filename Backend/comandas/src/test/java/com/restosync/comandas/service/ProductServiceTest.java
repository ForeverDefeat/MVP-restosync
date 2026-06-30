package com.restosync.comandas.service;

import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.dto.response.ProductResponse;
import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.mapper.ProductMapper;
import com.restosync.comandas.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @Mock
    AuditService auditService;

    @InjectMocks
    ProductService productService;

    @Test
    void toggleDisponibilidadInvierteEstadoYAudita() {
        User admin = TestDataFactory.user("admin@test.local", UserRole.ADMINISTRADOR, "encoded");
        admin.setId(1L);
        Product product = TestDataFactory.product("Chicha morada", ProductCategory.BEBIDA, true);
        product.setId(8L);
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .available(false)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.toggleDisponibilidad(product.getId(), admin);

        assertThat(product.getAvailable()).isFalse();
        assertThat(result.getAvailable()).isFalse();
        verify(auditService).log(any(), any(), any(), any());
    }
}
