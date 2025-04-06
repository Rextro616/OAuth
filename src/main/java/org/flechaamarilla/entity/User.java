package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Date;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;  // Contraseña hasheada

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public UserRole role;

    @Column
    public String email;

    @Column
    public String displayName;

    @Column
    public String profileBio;

    @Column
    public String profileImageUrl;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date lastLogin;

    @Column
    public String fcmToken;

    // Constructor for easier entity creation
    public User() {
        this.createdAt = new Date();
    }

    // UserRole enum para mejor tipado que un simple String
    public enum UserRole {
        CREATOR,    // Puede publicar mangas
        READER,     // Usuario básico que solo lee
        MODERATOR,  // Puede moderar contenido
        ADMIN       // Acceso completo
    }
}