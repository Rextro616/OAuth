package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO para actualizar un capítulo existente
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterUpdateDTO {

    public String title;

    public String description;

    public List<String> pages;

    public Boolean draft;
}
