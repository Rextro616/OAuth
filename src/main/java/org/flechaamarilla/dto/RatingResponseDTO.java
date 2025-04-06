package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO para respuesta de calificación
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponseDTO {

    public Long id;

    public Long mangaId;

    public String mangaTitle;

    public int score;

    public Date ratedAt;
}
