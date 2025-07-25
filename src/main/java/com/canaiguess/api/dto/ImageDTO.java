package com.canaiguess.api.dto;

import com.canaiguess.api.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private String id;
    private String url;

    public static ImageDTO from(Image image) {
        return new ImageDTO(
                image.getPublicId(),
                image.getUrl()
        );
    }
}

