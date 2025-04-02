package org.flechaamarilla.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;

@Entity
public class User extends PanacheEntity {

    @Column(unique = true)
    public String username;

    public String password;  // Guardaremos la contraseña hasheada

    public String rol; // Ejemplo: "ADMIN", "CLIENTE"
}
