package com.ahmed.picturepublishingservice.picture.controllers;

import com.ahmed.picturepublishingservice.picture.dtos.PictureResponse;
import com.ahmed.picturepublishingservice.picture.service.PictureService;
import com.ahmed.picturepublishingservice.picture.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureService pictureService;
    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PictureResponse> uploadPicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(pictureService.uploadPicture(file, title, userDetails.getUsername()));
    }

    @GetMapping("/landing")
    public ResponseEntity<List<PictureResponse>> getLandingPictures() {
        // 🌟 تعديل مهم: نخليه ينادي الميثود اللي بتجيب الـ Approved بس
        return ResponseEntity.ok(pictureService.getAllApprovedPicturesForLanding());
    }

    @GetMapping("/view/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            Path file = storageService.load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
