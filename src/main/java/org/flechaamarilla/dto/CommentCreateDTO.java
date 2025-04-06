package org.flechaamarilla.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO para crear/enviar un comentario
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDTO {

    @NotNull(message = "El ID del capítulo es obligatorio")
    public Long chapterId;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    public String content;
}

