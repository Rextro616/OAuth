package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO para respuesta de comentario
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

    public Long id;

    public UserInfoDTO user;

    public Long chapterId;

    public String content;

    public Date createdAt;

    public boolean edited;
}
