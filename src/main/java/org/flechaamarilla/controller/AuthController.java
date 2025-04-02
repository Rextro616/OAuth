package org.flechaamarilla.controller;


import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.flechaamarilla.dto.LoginDTO;
import org.flechaamarilla.dto.TokenResponseDTO;
import org.flechaamarilla.dto.UserRegisterDTO;
import org.flechaamarilla.service.AuthService;

@Path("/auth")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    AuthService authService;

    @POST
    @Path("/register")
    @PermitAll
    public void userRegister(@Valid UserRegisterDTO userRegisterDTO) {
        authService.registerUser(userRegisterDTO.username, userRegisterDTO.password, userRegisterDTO.rol);
    }

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponseDTO login(@Valid LoginDTO loginDTO) {
        String token = authService.authenticateUser(loginDTO.username, loginDTO.password);
        return  TokenResponseDTO.builder().token(token).build();
    }
}
