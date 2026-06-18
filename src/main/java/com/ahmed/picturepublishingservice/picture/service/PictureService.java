package com.ahmed.picturepublishingservice.picture.service;

import com.ahmed.picturepublishingservice.picture.dtos.PictureResponse;
import com.ahmed.picturepublishingservice.picture.entities.Picture;
import com.ahmed.picturepublishingservice.picture.entities.PictureStatus;
import com.ahmed.picturepublishingservice.picture.repo.PictureRepository;
import com.ahmed.picturepublishingservice.user.User;
import com.ahmed.picturepublishingservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PictureService {

    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public PictureResponse uploadPicture(MultipartFile file, String title, String userEmail) {
        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found with email: " + userEmail));

        String uniqueFileName = storageService.store(file);
        String filePath = storageService.load(uniqueFileName).toString();

        Picture picture = Picture.builder()
                .title(title)
                .fileName(uniqueFileName)
                .filePath(filePath)
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(admin)
                .build();

        Picture savedPicture = pictureRepository.save(picture);
        log.info("Picture metadata saved in DB successfully with ID: {}", savedPicture.getId());

        return mapToResponse(savedPicture);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "approvedPictures")
    public List<PictureResponse> getAllApprovedPicturesForLanding() {
        return pictureRepository.findByStatus(PictureStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PictureResponse mapToResponse(Picture picture) {
        return PictureResponse.builder()
                .id(picture.getId())
                .title(picture.getTitle())
                .fileName(picture.getFileName())
                .fileUrl("/api/v1/pictures/view/" + picture.getFileName())
                .uploadedAt(picture.getUploadedAt())
                .uploadedBy(picture.getUploadedBy().getEmail())
                .status(picture.getStatus())
                .build();
    }

    public List<PictureResponse> getPicturesByStatus(PictureStatus status) {
        return pictureRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "approvedPictures", allEntries = true)
    public PictureResponse reviewPicture(Long id, PictureStatus status) {
        Picture picture = pictureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Picture not found"));
        picture.setStatus(status);
        Picture updatedPicture = pictureRepository.save(picture);
        return mapToResponse(updatedPicture);
    }
}