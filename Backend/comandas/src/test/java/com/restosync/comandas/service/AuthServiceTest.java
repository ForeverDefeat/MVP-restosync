package com.restosync.comandas.service;

import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.dto.response.AuthResponse;
import com.restosync.comandas.dto.response.UserResponse;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.mapper.UserMapper;
import com.restosync.comandas.repository.UserRepository;
import com.restosync.comandas.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    UserMapper userMapper;

    @Mock
    AuditService auditService;

    @InjectMocks
    AuthService authService;

    @Test
    void loginGeneraJwtYRegistraAuditoria() {
        User user = TestDataFactory.user("mesero@test.local", UserRole.MESERO, "encoded");
        user.setId(10L);
        UserResponse response = UserResponse.builder()
                .id(10L)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .active(true)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId())).thenReturn("jwt-token");
        when(jwtUtil.getExpirationMillis()).thenReturn(86_400_000L);
        when(userMapper.toResponse(user)).thenReturn(response);

        AuthResponse result = authService.login(TestDataFactory.loginRequest(user.getEmail(), "123456"));

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(86_400L);
        assertThat(result.getUser().getEmail()).isEqualTo(user.getEmail());
        verify(authenticationManager).authenticate(any());
        verify(auditService).log(AuditService.LOGIN_SUCCESS, user.getId(),
                java.util.Map.of("email", user.getEmail(), "role", user.getRole().name()));
    }

    @Test
    void loginBloqueaUsuariosInactivos() {
        User user = TestDataFactory.user("inactive@test.local", UserRole.MESERO, "encoded");
        user.setActive(false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(TestDataFactory.loginRequest(user.getEmail(), "123456")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("desactivada");
    }
}
