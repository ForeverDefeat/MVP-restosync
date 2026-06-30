package com.restosync.comandas.security;

import com.restosync.comandas.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
 
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
 
/**
 * Utilidad para generación, validación y extracción de claims de tokens JWT.
 *
 * Estructura del token:
 *   Header  : { alg: HS256, typ: JWT }
 *   Payload : { sub: email, role: MESERO, userId: 1, iat: ..., exp: ... }
 *   Signature: HMAC-SHA256
 *
 * Propiedades leídas de application.properties:
 *   jwt.secret     → clave secreta en Base64 (mínimo 256 bits)
 *   jwt.expiration → milisegundos hasta expiración (ej: 86400000 = 24h)
 */
@Component
@Slf4j
public class JwtUtil {
 
    @Value("${jwt.secret}")
    private String secret;
 
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${spring.profiles.active:dev}")
    private String activeProfiles;

    @PostConstruct
    void validateConfiguration() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 32 caracteres.");
        }
        if (activeProfiles.contains("prod") && secret.contains("dev-secret")) {
            throw new IllegalStateException("JWT_SECRET de produccion no puede usar el secreto de desarrollo.");
        }
    }
 
    // ── Generación ────────────────────────────────────────────────────────────
 
    /**
     * Genera un token JWT con el email como subject y el rol + userId como claims extra.
     */
    public String generateToken(String email, UserRole role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",   role.name());
        claims.put("userId", userId);
        return buildToken(claims, email);
    }
 
    private String buildToken(Map<String, Object> extraClaims, String subject) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expiration);
 
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
 
    // ── Validación ────────────────────────────────────────────────────────────
 
    /**
     * Valida el token: firma, expiración y coincidencia de subject con UserDetails.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }
 
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
 
    // ── Extracción de claims ──────────────────────────────────────────────────
 
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
 
    public UserRole extractRole(String token) {
        String roleStr = extractClaim(token, claims -> claims.get("role", String.class));
        return UserRole.valueOf(roleStr);
    }
 
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class)).longValue();
    }
 
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
 
    public Long getExpirationMillis() {
        return expiration;
    }
 
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
 
    // ── Internos ──────────────────────────────────────────────────────────────
 
    private Claims extractAllClaims(String token) {
        // jjwt 0.11.x: use parserBuilder() -> setSigningKey() -> build() -> parseClaimsJws()
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
 
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
