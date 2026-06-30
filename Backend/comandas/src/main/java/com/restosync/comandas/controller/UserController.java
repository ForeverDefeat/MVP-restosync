package com.restosync.comandas.controller;

import com.restosync.comandas.dto.request.CreateUserRequest;
import com.restosync.comandas.dto.request.UpdateUserRoleRequest;
import com.restosync.comandas.dto.response.ApiResponse;
import com.restosync.comandas.dto.response.UserResponse;
import com.restosync.comandas.security.SecurityUser;
import com.restosync.comandas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
/**
 * Endpoints de gestión de usuarios y roles — acceso exclusivo ADMINISTRADOR.
 *
 * GET    /api/users              → listar todos los usuarios activos
 * GET    /api/users/{id}         → detalle de un usuario
 * POST   /api/users              → crear usuario
 * PATCH  /api/users/{id}/role    → cambiar rol
 * PATCH  /api/users/{id}/active  → activar / desactivar
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Validated
@Tag(name = "Usuarios y roles", description = "Gestión de personal y permisos")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
 
    private final UserService userService;
 
    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        // Fix: el service expone listarTodos(), no listar()
        List<UserResponse> users = userService.listarTodos();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }
 
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @PathVariable @Positive(message = "El ID del usuario debe ser mayor a cero") Long id) {
        // Fix: el service expone buscarPorId(), no obtenerPorId()
        UserResponse user = userService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
 
    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        UserResponse created = userService.crear(request, principal.getUser());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Usuario creado correctamente"));
    }
 
    @PatchMapping("/{id}/role")
    @Operation(summary = "Cambiar rol de usuario",
               description = "El usuario deberá iniciar sesión nuevamente para aplicar el nuevo rol")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable @Positive(message = "El ID del usuario debe ser mayor a cero") Long id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
 
        UserResponse updated = userService.actualizarRol(id, request.getRole(), principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Rol actualizado correctamente"));
    }
 
    @PatchMapping("/{id}/active")
    @Operation(summary = "Activar o desactivar usuario")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(
            @PathVariable @Positive(message = "El ID del usuario debe ser mayor a cero") Long id,
            @AuthenticationPrincipal SecurityUser principal) {
 
        UserResponse updated = userService.toggleActivo(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(updated, "Estado de usuario actualizado"));
    }
}
