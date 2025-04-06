package org.flechaamarilla.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

// DTO para crear un nuevo manga
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MangaCreateDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    public String title;

    @Size(max = 2000, message = "La descripción no puede exceder los 2000 caracteres")
    public String description;

    public String coverImageUrl;

    public Set<String> genres;
}

