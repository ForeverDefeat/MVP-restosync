package com.restosync.comandas.service;

import com.restosync.comandas.dto.request.CreateUserRequest;
import com.restosync.comandas.dto.response.UserResponse;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.exception.ResourceNotFoundException;
import com.restosync.comandas.mapper.UserMapper;
import com.restosync.comandas.repository.UserRepository;
import com.restosync.comandas.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.Map;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
 
    private final UserRepository  userRepository;
    private final UserMapper      userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService    auditService;
 
    // ── Consultas ────────────────────────────────────────────────────────────
 
    @Transactional(readOnly = true)
    public List<UserResponse> listarTodos() {
        return userMapper.toResponseList(
                userRepository.findAllByActiveOrderByNameAsc(true)
        );
    }
 
    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        return userMapper.toResponse(obtenerEntidad(id));
    }
 
    // ── Creación ─────────────────────────────────────────────────────────────
 
    @Transactional
    public UserResponse crear(CreateUserRequest request, User currentUser) {
        String email = TextNormalizer.email(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(
                    "Ya existe un usuario registrado con el email: " + email
            );
        }
 
        User user = User.builder()
                .name(TextNormalizer.required(request.getName()))
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();
 
        user = userRepository.save(user);
 
        auditService.log(
                AuditService.USER_CREATED,
                currentUser.getId(),
                null,
                Map.of("newUserId", user.getId(), "role", user.getRole().name())
        );
 
        log.info("Usuario creado: id={} email='{}' rol={}", user.getId(), user.getEmail(), user.getRole());
        return userMapper.toResponse(user);
    }
 
    // ── Cambiar rol ──────────────────────────────────────────────────────────
 
    @Transactional
    public UserResponse actualizarRol(Long id, UserRole nuevoRol, User currentUser) {
        User user = obtenerEntidad(id);

        if (user.getRole() == UserRole.ADMINISTRADOR && nuevoRol != UserRole.ADMINISTRADOR && esUltimoAdminActivo(user)) {
            throw new BusinessException("No se puede cambiar el rol del ultimo administrador activo.");
        }
 
        UserRole rolAnterior = user.getRole();
        user.setRole(nuevoRol);
        user = userRepository.save(user);
 
        auditService.log(
                AuditService.USER_ROLE_CHANGED,
                currentUser.getId(),
                null,
                Map.of(
                        "targetUserId", user.getId(),
                        "from", rolAnterior.name(),
                        "to",   nuevoRol.name()
                )
        );
 
        log.info("Rol de usuario {} cambiado: {} → {}", user.getId(), rolAnterior, nuevoRol);
        return userMapper.toResponse(user);
    }
 
    // ── Activar / Desactivar ─────────────────────────────────────────────────
 
    @Transactional
    public UserResponse toggleActivo(Long id, User currentUser) {
        User user = obtenerEntidad(id);
        if (user.getId().equals(currentUser.getId()) && user.getActive()) {
            throw new BusinessException("No puedes desactivar tu propio usuario.");
        }
        if (user.getActive() && esUltimoAdminActivo(user)) {
            throw new BusinessException("No se puede desactivar el ultimo administrador activo.");
        }
        boolean nuevoEstado = !user.getActive();
        user.setActive(nuevoEstado);
        user = userRepository.save(user);
 
        auditService.log(
                nuevoEstado ? AuditService.USER_ACTIVATED : AuditService.USER_DEACTIVATED,
                currentUser.getId(),
                null,
                Map.of("targetUserId", user.getId(), "active", nuevoEstado)
        );
 
        log.info("Usuario {} {}", user.getId(), nuevoEstado ? "activado" : "desactivado");
        return userMapper.toResponse(user);
    }
 
    // ── Utilitario interno ───────────────────────────────────────────────────
 
    public User obtenerEntidad(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private boolean esUltimoAdminActivo(User user) {
        return user.getRole() == UserRole.ADMINISTRADOR
                && Boolean.TRUE.equals(user.getActive())
                && userRepository.countByRoleAndActive(UserRole.ADMINISTRADOR, true) <= 1;
    }
}
