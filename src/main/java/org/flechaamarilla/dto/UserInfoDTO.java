package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO básico para información resumida del usuario (para listas, autor del manga, etc.)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    public Long id;

    public String username;

    public String displayName;

    public String profileImageUrl;
}
