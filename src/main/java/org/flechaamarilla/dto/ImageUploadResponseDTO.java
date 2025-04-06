package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadResponseDTO {
    public String publicId;
    public String url;
    public String secureUrl;
    public String format;
    public int width;
    public int height;
    public long size;
    public String resourceType;
}