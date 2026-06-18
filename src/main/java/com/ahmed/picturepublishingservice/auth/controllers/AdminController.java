package com.ahmed.picturepublishingservice.auth.controllers;

import com.ahmed.picturepublishingservice.picture.dtos.PictureResponse;
import com.ahmed.picturepublishingservice.picture.entities.PictureStatus;
import com.ahmed.picturepublishingservice.picture.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pictures")
@RequiredArgsConstructor
public class AdminController {

    private final PictureService pictureService;

    @GetMapping("/pending")
    public ResponseEntity<List<PictureResponse>> getPendingPictures() {
        return ResponseEntity.ok(pictureService.getPicturesByStatus(PictureStatus.PENDING));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<PictureResponse> reviewPicture(
            @PathVariable Long id,
            @RequestParam PictureStatus status) {
        return ResponseEntity.ok(pictureService.reviewPicture(id, status));
    }
}