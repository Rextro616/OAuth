package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO para listar capítulos (sin incluir las páginas)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterListItemDTO {

    public Long id;

    public Long mangaId;

    public String title;

    public int chapterNumber;

    public Date publishDate;

    public long viewCount;
}
