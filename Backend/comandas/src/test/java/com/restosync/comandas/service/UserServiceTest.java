package com.restosync.comandas.service;

import com.restosync.comandas.TestDataFactory;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.exception.BusinessException;
import com.restosync.comandas.mapper.UserMapper;
import com.restosync.comandas.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuditService auditService;

    @InjectMocks
    UserService userService;

    @Test
    void noPermiteDesactivarUsuarioActual() {
        User admin = TestDataFactory.user("admin@test.local", UserRole.ADMINISTRADOR, "encoded");
        admin.setId(1L);

        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.toggleActivo(admin.getId(), admin))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("propio usuario");
    }

    @Test
    void noPermiteCambiarRolDelUltimoAdminActivo() {
        User admin = TestDataFactory.user("admin@test.local", UserRole.ADMINISTRADOR, "encoded");
        admin.setId(1L);

        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(userRepository.countByRoleAndActive(UserRole.ADMINISTRADOR, true)).thenReturn(1L);

        assertThatThrownBy(() -> userService.actualizarRol(admin.getId(), UserRole.MESERO, admin))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ultimo administrador");
    }
}
