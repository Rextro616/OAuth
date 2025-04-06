package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// DTO para actualizar un manga existente
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MangaUpdateDTO {

    public String title;

    public String description;

    public String coverImageUrl;

    public Set<String> genres;

    public Boolean completed;
}
