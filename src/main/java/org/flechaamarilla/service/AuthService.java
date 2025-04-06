package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.AllArgsConstructor;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.Date;

@ApplicationScoped
@AllArgsConstructor
public class AuthService {

    UserRepository userRepository;
    JwtService jwtService;
    FirebaseService firebaseService;

    @Transactional
    public User updateFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BadRequestException("Usuario no encontrado");
        }

        user.fcmToken = fcmToken;
        userRepository.persist(user);

        return user;
    }

    @Transactional
    public User registerUser(String username, String password, String email, String displayName, User.UserRole role) {
        // Validar que el nombre de usuario no exista
        if (userRepository.findByUsername(username) != null) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }

        // Validar que el email no exista si se proporciona
        if (email != null && !email.isBlank() && userRepository.findByEmail(email) != null) {
            throw new BadRequestException("El correo electrónico ya está en uso");
        }

        User user = new User();
        user.username = username;
        user.password = BCrypt.hashpw(password, BCrypt.gensalt()); // Hashear la contraseña
        user.email = email;
        user.displayName = displayName != null ? displayName : username;
        user.role = role;
        user.createdAt = new Date();
        user.lastLogin = new Date();

        userRepository.persist(user);
        return user;
    }

    public String authenticateUser(String username, String password, String fcmToken) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.password)) {
            throw new NotAuthorizedException("Credenciales inválidas");
        }

        // Actualizar último login
        user.lastLogin = new Date();

        // Actualizar FCM token si se proporciona
        if (fcmToken != null && !fcmToken.isEmpty()) {
            user.fcmToken = fcmToken;

            // Suscribir al usuario a temas según su rol
            if (firebaseService.isInitialized()) {
                firebaseService.subscribeToTopic(Arrays.asList(fcmToken), "all_users");
                firebaseService.subscribeToTopic(Arrays.asList(fcmToken), "role_" + user.role.toString().toLowerCase());
            }
        }

        userRepository.persist(user);

        // Generar el token JWT incluyendo el ID y el rol del usuario
        return jwtService.generateToken(user.username, user.id.toString(), user.role.toString());
    }

    public User getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotAuthorizedException("Usuario no encontrado");
        }
        return user;
    }

    public User getCurrentUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotAuthorizedException("Usuario no encontrado");
        }
        return user;
    }

    @Transactional
    public User updateUserProfile(Long userId, String email, String displayName, String profileBio, String profileImageUrl) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BadRequestException("Usuario no encontrado");
        }

        // Actualizar campos si se proporcionan
        if (email != null && !email.equals(user.email)) {
            // Verificar que el nuevo email no esté en uso
            User existingEmail = userRepository.findByEmail(email);
            if (existingEmail != null && !existingEmail.id.equals(userId)) {
                throw new BadRequestException("El correo electrónico ya está en uso");
            }
            user.email = email;
        }

        if (displayName != null) {
            user.displayName = displayName;
        }

        if (profileBio != null) {
            user.profileBio = profileBio;
        }

        if (profileImageUrl != null) {
            user.profileImageUrl = profileImageUrl;
        }

        userRepository.persist(user);
        return user;
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BadRequestException("Usuario no encontrado");
        }

        // Verificar contraseña actual
        if (!BCrypt.checkpw(currentPassword, user.password)) {
            throw new NotAuthorizedException("Contraseña actual incorrecta");
        }

        // Actualizar a nueva contraseña
        user.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userRepository.persist(user);
    }
}