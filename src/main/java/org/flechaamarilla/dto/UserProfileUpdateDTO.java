package org.flechaamarilla.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para actualizar perfil de usuario
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {

    @Email(message = "Formato de correo electrónico inválido")
    public String email;

    @Size(max = 50, message = "El nombre de visualización no puede exceder los 50 caracteres")
    public String displayName;

    @Size(max = 500, message = "La biografía no puede exceder los 500 caracteres")
    public String profileBio;

    public String profileImageUrl;
}
