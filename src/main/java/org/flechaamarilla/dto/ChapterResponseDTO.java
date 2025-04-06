package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

// DTO para responder con información del capítulo
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponseDTO {

    public Long id;

    public Long mangaId;

    public String mangaTitle;

    public String title;

    public int chapterNumber;

    public String description;

    public Date publishDate;

    public List<String> pages;

    public long viewCount;

    public boolean draft;
}
