package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.*;

import java.util.List;

// Actualización del UserRepository
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public List<User> findCreators(int page, int size) {
        return find("role", User.UserRole.CREATOR)
                .page(page, size)
                .list();
    }
}

