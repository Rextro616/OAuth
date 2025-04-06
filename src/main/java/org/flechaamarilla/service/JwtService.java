package org.flechaamarilla.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    public String generateToken(String username, String userId, String role) {
        return Jwt.issuer("monogatari-auth")
                .subject(username)
                .upn(username)  // user principal name (nombre de usuario)
                .groups(Set.of(role)) // Grupos son los roles en Quarkus
                .claim("userId", userId) // ID del usuario como claim personalizado
                .expiresAt(System.currentTimeMillis() / 1000 + 86400) // Expira en 24 horas
                .sign();
    }
}