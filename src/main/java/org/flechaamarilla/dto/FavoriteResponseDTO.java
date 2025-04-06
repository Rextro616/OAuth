package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO para respuesta de favoritos
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponseDTO {

    public Long id;

    public MangaResponseDTO manga;

    public Date addedAt;

    public int lastReadChapter;
}
