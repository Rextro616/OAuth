package org.flechaamarilla.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.AllArgsConstructor;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
@AllArgsConstructor
public class AuthService {


    UserRepository userRepository;

    JwtService jwtService;

    @Transactional
    public void registerUser(String username, String password, String rol) {
        User user = new User();
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt()); // Hashear la contraseña
        user.rol = rol;
        user.persist();
    }

    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.password)) {
            throw new NotAuthorizedException("Credenciales inválidas");
        }
        return jwtService.generateToken(user.username, user.rol);
    }
}
