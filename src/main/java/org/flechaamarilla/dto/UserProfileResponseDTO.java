package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {

    public Long id;

    public String username;

    public String email;

    public String displayName;

    public String profileBio;

    public String profileImageUrl;

    public String role;

    public Date createdAt;

    public Date lastLogin;

    public String fcmToken;
}