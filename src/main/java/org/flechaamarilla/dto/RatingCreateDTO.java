package org.flechaamarilla.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para calificar un manga
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingCreateDTO {

    @NotNull(message = "El ID del manga es obligatorio")
    public Long mangaId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    public Integer score;
}
