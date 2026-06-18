package com.ahmed.picturepublishingservice.picture.dtos;

import com.ahmed.picturepublishingservice.picture.entities.PictureStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PictureResponse {
    private Long id;
    private String title;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private PictureStatus status;
}