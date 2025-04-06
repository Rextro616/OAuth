package org.flechaamarilla.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

// DTO para crear un nuevo capítulo
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterCreateDTO {

    @NotNull(message = "El ID del manga es obligatorio")
    public Long mangaId;

    @NotBlank(message = "El título es obligatorio")
    public String title;

    @NotNull(message = "El número de capítulo es obligatorio")
    @Min(value = 1, message = "El número de capítulo debe ser mayor que 0")
    public Integer chapterNumber;

    public String description;

    @NotEmpty(message = "Debe incluir al menos una página")
    public List<String> pages;

    public Boolean draft;
}

