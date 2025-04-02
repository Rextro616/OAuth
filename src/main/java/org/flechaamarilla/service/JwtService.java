package org.flechaamarilla.service;


import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    public String generateToken(String username, String role) {
        return Jwt.issuer("auth-service")
                .subject(username)
                .groups(Set.of(role)) // Grupos son los roles en Quarkus
                .expiresAt(System.currentTimeMillis() / 1000 + 3600) // Expira en 1 hora
                .sign();
    }
}
