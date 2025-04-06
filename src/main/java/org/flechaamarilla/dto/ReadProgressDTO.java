package org.flechaamarilla.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para actualizar progreso de lectura
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadProgressDTO {

    @NotNull(message = "El ID del manga es obligatorio")
    public Long mangaId;

    @NotNull(message = "El número de capítulo es obligatorio")
    @Min(value = 1, message = "El número de capítulo debe ser mayor que 0")
    public Integer lastReadChapter;
}
