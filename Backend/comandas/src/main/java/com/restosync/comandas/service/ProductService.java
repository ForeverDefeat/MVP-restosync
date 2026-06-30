package com.restosync.comandas.service;

import com.restosync.comandas.dto.request.CreateProductRequest;
import com.restosync.comandas.dto.response.ProductResponse;
import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.exception.ResourceNotFoundException;
import com.restosync.comandas.mapper.ProductMapper;
import com.restosync.comandas.repository.ProductRepository;
import com.restosync.comandas.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.Map;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
 
    private final ProductRepository productRepository;
    private final ProductMapper     productMapper;
    private final AuditService      auditService;
 
    // ── Consultas ────────────────────────────────────────────────────────────
 
    @Transactional(readOnly = true)
    public List<ProductResponse> buscar(String search, ProductCategory category, Boolean available) {
        return productMapper.toResponseList(
                productRepository.searchProducts(TextNormalizer.nullable(search), category, available)
        );
    }
 
    @Transactional(readOnly = true)
    public ProductResponse buscarPorId(Long id) {
        return productMapper.toResponse(obtenerEntidad(id));
    }
 
    // ── Creación ─────────────────────────────────────────────────────────────
 
    @Transactional
    public ProductResponse crear(CreateProductRequest request, User currentUser) {
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
 
        auditService.log(
                AuditService.PRODUCT_CREATED,
                currentUser.getId(),
                null,
                Map.of("productId", product.getId(), "name", product.getName())
        );
 
        log.info("Producto creado: id={} nombre='{}'", product.getId(), product.getName());
        return productMapper.toResponse(product);
    }
 
    // ── Actualización ────────────────────────────────────────────────────────
 
    @Transactional
    public ProductResponse actualizar(Long id, CreateProductRequest request, User currentUser) {
        Product product = obtenerEntidad(id);
        productMapper.updateEntity(request, product);
        product = productRepository.save(product);
 
        auditService.log(
                AuditService.PRODUCT_UPDATED,
                currentUser.getId(),
                null,
                Map.of("productId", product.getId(), "name", product.getName())
        );
 
        return productMapper.toResponse(product);
    }
 
    // ── Activar / Desactivar (lógico) ────────────────────────────────────────
 
    @Transactional
    public ProductResponse toggleDisponibilidad(Long id, User currentUser) {
        Product product = obtenerEntidad(id);
        boolean nuevoEstado = !product.getAvailable();
        product.setAvailable(nuevoEstado);
        product = productRepository.save(product);
 
        String action = nuevoEstado ? AuditService.PRODUCT_UPDATED : AuditService.PRODUCT_DEACTIVATED;
        auditService.log(
                action,
                currentUser.getId(),
                null,
                Map.of("productId", product.getId(), "available", nuevoEstado)
        );
 
        log.info("Producto {}: id={} nombre='{}'",
                nuevoEstado ? "activado" : "desactivado", product.getId(), product.getName());
        return productMapper.toResponse(product);
    }
 
    // ── Utilitario interno ───────────────────────────────────────────────────
 
    private Product obtenerEntidad(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
