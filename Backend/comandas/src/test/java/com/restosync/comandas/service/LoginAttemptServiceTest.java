package com.restosync.comandas.service;

import com.restosync.comandas.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginAttemptServiceTest {

    @Test
    void bloqueaDespuesDeCincoIntentosFallidos() {
        LoginAttemptService service = new LoginAttemptService();

        for (int i = 0; i < 5; i++) {
            service.recordFailure("admin@test.local", "127.0.0.1");
        }

        assertThatThrownBy(() -> service.assertNotBlocked("admin@test.local", "127.0.0.1"))
                .isInstanceOf(TooManyLoginAttemptsException.class);
    }

    @Test
    void loginExitosoLimpiaIntentosFallidos() {
        LoginAttemptService service = new LoginAttemptService();

        for (int i = 0; i < 5; i++) {
            service.recordFailure("admin@test.local", "127.0.0.1");
        }
        service.recordSuccess("admin@test.local", "127.0.0.1");

        service.assertNotBlocked("admin@test.local", "127.0.0.1");
    }
}
