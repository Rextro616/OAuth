package org.flechaamarilla.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.flechaamarilla.dto.*;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.service.AuthService;
import org.flechaamarilla.service.FirebaseService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticación", description = "Operaciones relacionadas con autenticación y usuarios")
public class AuthController {

    AuthService authService;
    FirebaseService firebaseService;

    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Registrar un nuevo usuario")
    public void userRegister(@Valid UserRegisterDTO userRegisterDTO) {
        // Convertir el string de rol a enum
        User.UserRole role;
        try {
            role = User.UserRole.valueOf(userRegisterDTO.rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol no válido. Roles permitidos: CREATOR, READER, MODERATOR, ADMIN");
        }

        authService.registerUser(
                userRegisterDTO.username,
                userRegisterDTO.password,
                userRegisterDTO.email,
                userRegisterDTO.displayName,
                role
        );
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Iniciar sesión")
    public TokenResponseDTO login(@Valid LoginDTO loginDTO) {
        String token = authService.authenticateUser(loginDTO.username, loginDTO.password, loginDTO.fcmToken);

        // Si se proporcionó un token FCM y el usuario se autenticó correctamente, 
        // enviar una notificación de bienvenida
        if (loginDTO.fcmToken != null && !loginDTO.fcmToken.isEmpty()) {
            User user = authService.getCurrentUser(loginDTO.username);

            String notificationTitle = "Bienvenido a Monogatari";
            String notificationBody = "Has iniciado sesión correctamente en la aplicación.";

            Map<String, String> data = new HashMap<>();
            data.put("action", "login");
            data.put("userId", user.id.toString());
            data.put("role", user.role.toString());

            firebaseService.sendNotification(loginDTO.fcmToken, notificationTitle, notificationBody, data);
        }

        return TokenResponseDTO.builder().token(token).build();
    }

    @GET
    @Path("/profile")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Obtener perfil del usuario actual")
    public UserProfileResponseDTO getUserProfile(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        User user = authService.getCurrentUser(username);

        return UserProfileResponseDTO.builder()
                .id(user.id)
                .username(user.username)
                .email(user.email)
                .displayName(user.displayName)
                .profileBio(user.profileBio)
                .profileImageUrl(user.profileImageUrl)
                .role(user.role.toString())
                .createdAt(user.createdAt)
                .lastLogin(user.lastLogin)
                .fcmToken(user.fcmToken)
                .build();
    }

    @PUT
    @Path("/profile")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Actualizar perfil del usuario actual")
    public UserProfileResponseDTO updateUserProfile(
            @Context SecurityContext securityContext,
            @Valid UserProfileUpdateDTO updateDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User user = authService.getCurrentUser(username);

        user = authService.updateUserProfile(
                user.id,
                updateDTO.email,
                updateDTO.displayName,
                updateDTO.profileBio,
                updateDTO.profileImageUrl
        );

        return UserProfileResponseDTO.builder()
                .id(user.id)
                .username(user.username)
                .email(user.email)
                .displayName(user.displayName)
                .profileBio(user.profileBio)
                .profileImageUrl(user.profileImageUrl)
                .role(user.role.toString())
                .createdAt(user.createdAt)
                .lastLogin(user.lastLogin)
                .fcmToken(user.fcmToken)
                .build();
    }

    @POST
    @Path("/change-password")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Cambiar contraseña del usuario actual")
    public void changePassword(
            @Context SecurityContext securityContext,
            @Valid PasswordChangeDTO passwordChangeDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User user = authService.getCurrentUser(username);

        authService.changePassword(
                user.id,
                passwordChangeDTO.currentPassword,
                passwordChangeDTO.newPassword
        );
    }

    @PUT
    @Path("/fcm-token")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Actualizar el token FCM del usuario")
    public void updateFcmToken(
            @Context SecurityContext securityContext,
            TokenResponseDTO tokenUpdateDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User user = authService.getCurrentUser(username);

        // Actualizar token FCM usando el servicio
        user = authService.updateFcmToken(user.id, tokenUpdateDTO.token);

        // Suscribir a temas relevantes
        if (firebaseService.isInitialized() && tokenUpdateDTO.token != null && !tokenUpdateDTO.token.isEmpty()) {
            firebaseService.subscribeToTopic(Arrays.asList(tokenUpdateDTO.token), "all_users");
            firebaseService.subscribeToTopic(Arrays.asList(tokenUpdateDTO.token), "role_" + user.role.toString().toLowerCase());
        }
    }
}