package org.flechaamarilla.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para añadir/quitar favoritos
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteCreateDTO {

    @NotNull(message = "El ID del manga es obligatorio")
    public Long mangaId;
}
