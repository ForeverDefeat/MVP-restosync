package com.restosync.comandas.service;

import com.restosync.comandas.dto.request.LoginRequest;
import com.restosync.comandas.dto.response.AuthResponse;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.exception.ResourceNotFoundException;
import com.restosync.comandas.mapper.UserMapper;
import com.restosync.comandas.repository.UserRepository;
import com.restosync.comandas.security.JwtUtil;
import com.restosync.comandas.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.Map;
 
/**
 * Gestiona la autenticación y generación de tokens JWT.
 *
 * Flujo de login:
 *   1. AuthenticationManager valida email + password contra la BD (vía UserDetailsServiceImpl)
 *   2. Spring Security lanza BadCredentialsException si son incorrectas
 *   3. Se verifica que el usuario esté activo (active = true)
 *   4. Se genera el JWT con email, rol y userId como claims
 *   5. Se registra el acceso en audit_logs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
 
    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final JwtUtil               jwtUtil;
    private final UserMapper            userMapper;
    private final AuditService          auditService;
 
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
 
        // 1. Valida credenciales — lanza BadCredentialsException si fallan
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        TextNormalizer.email(request.getEmail()),
                        request.getPassword()
                )
        );
 
        // 2. Carga el usuario completo desde la BD
        String email = TextNormalizer.email(request.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
 
        // 3. Verifica que el usuario esté activo
        if (!user.getActive()) {
            throw new BusinessException("La cuenta está desactivada. Contacte al administrador.");
        }
 
        // 4. Genera el JWT incluyendo rol y userId como claims
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getId()
        );
 
        // 5. Audit log asíncrono
        auditService.log(
                AuditService.LOGIN_SUCCESS,
                user.getId(),
                Map.of("email", user.getEmail(), "role", user.getRole().name())
        );
 
        log.info("Login exitoso: email='{}' rol={}", user.getEmail(), user.getRole());
 
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMillis() / 1000)
                .user(userMapper.toResponse(user))
                .build();
    }
}
