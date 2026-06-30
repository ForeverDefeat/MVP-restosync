package com.restosync.comandas.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    @Test
    void rechazaSecretCorto() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "short");
        ReflectionTestUtils.setField(jwtUtil, "activeProfiles", "dev");

        assertThatThrownBy(jwtUtil::validateConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 caracteres");
    }

    @Test
    void rechazaSecretDeDesarrolloEnProd() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "restosync-dev-secret-change-me-minimum-256-bits-long");
        ReflectionTestUtils.setField(jwtUtil, "activeProfiles", "prod");

        assertThatThrownBy(jwtUtil::validateConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("desarrollo");
    }
}
