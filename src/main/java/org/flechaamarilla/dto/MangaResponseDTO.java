package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

// DTO para responder con información del manga
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MangaResponseDTO {

    public Long id;

    public String title;

    public String description;

    public String coverImageUrl;

    public UserInfoDTO creator;

    public Date createdAt;

    public Date updatedAt;

    public boolean completed;

    public Set<String> genres;

    public long viewCount;

    public float averageRating;

    public int ratingCount;

    public int chapterCount;

    public boolean inUserFavorites;
}
