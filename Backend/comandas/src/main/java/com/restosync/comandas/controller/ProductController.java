package com.restosync.comandas.controller;

import com.restosync.comandas.dto.request.CreateProductRequest;
import com.restosync.comandas.dto.response.ApiResponse;
import com.restosync.comandas.dto.response.ProductResponse;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.security.SecurityUser;
import com.restosync.comandas.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
/**
 * Endpoints del catálogo de productos.
 *
 * GET    /api/products                    → todos los roles autenticados
 * GET    /api/products/{id}               → todos los roles autenticados
 * POST   /api/products                    → solo ADMINISTRADOR
 * PUT    /api/products/{id}               → solo ADMINISTRADOR
 * PATCH  /api/products/{id}/availability  → solo ADMINISTRADOR
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Catálogo de productos", description = "Gestión del menú — platos y bebidas")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
 
    private final ProductService productService;
 
    // ── Lectura (todos los roles autenticados) ────────────────────────────────
 
    @GetMapping
    @Operation(summary = "Listar productos",
               description = "Filtra por categoría, disponibilidad y texto de búsqueda")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Boolean         available,
            @RequestParam(required = false) @Size(max = 100, message = "La busqueda no puede superar 100 caracteres")
            String search) {
 
        List<ProductResponse> products = productService.buscar(search, category, available);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }
 
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable @Positive(message = "El ID del producto debe ser mayor a cero") Long id) {
        // Fix: el service expone buscarPorId(), no obtenerPorId()
        ProductResponse product = productService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }
 
    // ── Escritura (solo ADMINISTRADOR) ────────────────────────────────────────
 
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request,
            // Fix: el service requiere currentUser para el audit log
            @AuthenticationPrincipal SecurityUser principal) {
 
        ProductResponse created = productService.crear(request, principal.getUser());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Producto creado correctamente"));
    }
 
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Editar producto completo")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable @Positive(message = "El ID del producto debe ser mayor a cero") Long id,
            @Valid @RequestBody CreateProductRequest request,
            // Fix: el service requiere currentUser para el audit log
            @AuthenticationPrincipal SecurityUser principal) {
 
        ProductResponse updated = productService.actualizar(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Producto actualizado correctamente"));
    }
 
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Activar o desactivar producto",
               description = "Desactivación lógica — el historial se preserva")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleAvailability(
            @PathVariable @Positive(message = "El ID del producto debe ser mayor a cero") Long id,
            // Fix: el service requiere currentUser para el audit log
            @AuthenticationPrincipal SecurityUser principal) {
 
        ProductResponse updated = productService.toggleDisponibilidad(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Disponibilidad actualizada"));
    }
}
