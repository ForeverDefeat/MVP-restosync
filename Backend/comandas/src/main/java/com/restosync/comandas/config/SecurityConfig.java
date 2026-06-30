package com.restosync.comandas.config;

import com.restosync.comandas.security.JwtAuthFilter;
import com.restosync.comandas.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de Spring Security.
 *
 * Decisiones de diseño:
 * - Stateless: sin sesiones HTTP (SessionCreationPolicy.STATELESS)
 * - JWT: el JwtAuthFilter valida el token en cada request
 * - CSRF deshabilitado: no aplica en APIs REST stateless
 * - @EnableMethodSecurity: habilita @PreAuthorize en controllers y services
 *
 * Reglas de acceso por endpoint:
 * PUBLIC → /api/auth/** (login, refresh)
 * PUBLIC → /ws/** (WebSocket handshake — el token se valida post-conexión)
 * PUBLIC → /swagger-ui/**, /v3/api-docs/**
 *
 * MESERO → POST /api/orders, PATCH /api/orders/{id}/items, DELETE
 * /api/orders/{id}
 * COCINERO → PATCH /api/orders/{id}/status (solo platos — validado en service)
 * BARTENDER → PATCH /api/orders/{id}/status (solo bebidas — validado en
 * service)
 * ADMIN → /api/products/**, /api/users/**
 * AUTENTICADO → GET /api/orders/**, GET /api/products/**
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final UserDetailsServiceImpl userDetailsService;

        @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:4173}")
        private String allowedOrigins;

        // ── Security filter chain ─────────────────────────────────────────────────

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Deshabilitar CSRF (API REST stateless)
                                .csrf(AbstractHttpConfigurer::disable)

                                // Configurar CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Sin sesiones HTTP
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Reglas de autorización por endpoint
                                .authorizeHttpRequests(auth -> auth

                                                // ── Endpoints públicos ────────────────────────────────────────
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/ws/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // ── Pedidos: acciones del Mesero ──────────────────────────────
                                                .requestMatchers(HttpMethod.POST, "/api/orders")
                                                .hasAnyRole("MESERO", "ADMINISTRADOR")
                                                .requestMatchers(HttpMethod.PATCH, "/api/orders/*/items")
                                                .hasAnyRole("MESERO", "ADMINISTRADOR")
                                                .requestMatchers(HttpMethod.DELETE, "/api/orders/*")
                                                .hasAnyRole("MESERO", "ADMINISTRADOR")

                                                // ── Pedidos: cambio de estado (Cocinero, Bartender, Mesero) ──
                                                // La validación fina de rol por categoría se hace en OrderService
                                                .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status")
                                                .hasAnyRole("COCINERO", "BARTENDER", "MESERO", "ADMINISTRADOR")

                                                // ── Pedidos: lectura (todos los roles autenticados) ────────────
                                                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()

                                                // ── Catálogo: lectura pública para el POS del Mesero ──────────
                                                .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()

                                                // ── Catálogo: escritura solo Admin ────────────────────────────
                                                .requestMatchers(HttpMethod.POST, "/api/products/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                                                .hasRole("ADMINISTRADOR")
                                                .requestMatchers(HttpMethod.PATCH, "/api/products/**")
                                                .hasRole("ADMINISTRADOR")

                                                // ── Usuarios: solo Admin ──────────────────────────────────────
                                                .requestMatchers("/api/users/**").hasRole("ADMINISTRADOR")

                                                // ── Cualquier otro endpoint requiere autenticación ────────────
                                                .anyRequest().authenticated())

                                // Proveedor de autenticación personalizado
                                .authenticationProvider(authenticationProvider())

                                // Añadir el filtro JWT antes del filtro estándar de Spring
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // ── Autenticación ─────────────────────────────────────────────────────────

        /**
         * Proveedor de autenticación DAO que valida credenciales
         * contra la BD usando UserDetailsService.
         */
        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder());
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // ── CORS ──────────────────────────────────────────────────────────────────

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                // Orígenes permitidos: frontend en dev y prod
                config.setAllowedOrigins(List.of(allowedOrigins.split(",")));

                config.setAllowedMethods(List.of(
                                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                config.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type",
                                "Accept",
                                "X-Requested-With"));

                // Permite que el frontend lea el header Authorization de la respuesta
                config.setExposedHeaders(List.of("Authorization"));

                // Necesario para enviar cookies o headers de autenticación
                config.setAllowCredentials(true);

                // Cachear preflight 1 hora
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
