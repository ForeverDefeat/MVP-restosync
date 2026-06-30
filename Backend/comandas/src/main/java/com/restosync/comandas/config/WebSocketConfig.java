package com.restosync.comandas.config;

import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.security.JwtUtil;
import com.restosync.comandas.security.SecurityUser;
import com.restosync.comandas.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
 
/**
 * Configuración del broker STOMP sobre SockJS para actualizaciones en tiempo real.
 *
 * Arquitectura de mensajería:
 *
 *   Frontend (STOMP client)
 *       │
 *       │  connect → ws://localhost:8080/ws
 *       │
 *       ▼
 *   WebSocket Server (Spring)
 *       │
 *       ├── subscribe /topic/orders/cocina      → panel KDS (platos)
 *       ├── subscribe /topic/orders/bar         → panel Bar (bebidas)
 *       └── subscribe /topic/orders/{waiterId}  → panel Mesero (sus comandas)
 *
 *   Backend publica con:
 *       messagingTemplate.convertAndSend("/topic/orders/cocina", orderResponse)
 *
 * Prefijos:
 *   /topic  → mensajes broadcast (uno a muchos, broker simple en memoria)
 *   /app    → mensajes dirigidos a @MessageMapping (si se necesita en el futuro)
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${spring.websocket.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;
 
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker simple en memoria para /topic (broadcast)
        registry.enableSimpleBroker("/topic");
 
        // Prefijo para métodos @MessageMapping en controladores WebSocket
        registry.setApplicationDestinationPrefixes("/app");
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            // Permite múltiples orígenes separados por coma
            .setAllowedOriginPatterns(allowedOrigins.split(","))
            // SockJS como fallback para navegadores que no soportan WebSocket nativo
            .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    UsernamePasswordAuthenticationToken authentication = authenticate(accessor);
                    accessor.setUser(authentication);
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    authorizeSubscription(accessor);
                }

                return message;
            }
        });
    }

    private UsernamePasswordAuthenticationToken authenticate(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException("Token JWT requerido para WebSocket.");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        String email = jwtUtil.extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtUtil.isTokenValid(token, userDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Token JWT invalido para WebSocket.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof UsernamePasswordAuthenticationToken authentication)
                || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new AccessDeniedException("Suscripcion WebSocket no autenticada.");
        }

        String destination = accessor.getDestination();
        if (destination == null) {
            throw new AccessDeniedException("Destino WebSocket invalido.");
        }

        UserRole role = securityUser.getRole();
        if (role == UserRole.ADMINISTRADOR) return;
        if (destination.equals("/topic/orders") || destination.equals("/topic/products")) return;
        if (destination.equals("/topic/orders/cocina") && role == UserRole.COCINERO) return;
        if (destination.equals("/topic/orders/bar") && role == UserRole.BARTENDER) return;
        if (destination.equals("/topic/orders/waiter/" + securityUser.getUserId()) && role == UserRole.MESERO) return;

        throw new AccessDeniedException("No tienes permiso para suscribirte a " + destination);
    }
}
