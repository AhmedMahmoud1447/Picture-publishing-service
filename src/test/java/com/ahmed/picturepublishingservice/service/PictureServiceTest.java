package com.ahmed.picturepublishingservice.service;


import com.ahmed.picturepublishingservice.picture.dtos.PictureResponse;
import com.ahmed.picturepublishingservice.picture.entities.Picture;
import com.ahmed.picturepublishingservice.picture.entities.PictureStatus;
import com.ahmed.picturepublishingservice.picture.repo.PictureRepository;
import com.ahmed.picturepublishingservice.picture.service.PictureService;
import com.ahmed.picturepublishingservice.picture.service.StorageService;
import com.ahmed.picturepublishingservice.user.User;
import com.ahmed.picturepublishingservice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PictureServiceTest {

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private PictureService pictureService;

    private User mockUser;
    private Picture mockPicture;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("ahmed@mail.com");

        mockPicture = new Picture();
        mockPicture.setId(100L);
        mockPicture.setTitle("Nature Pic");
        mockPicture.setFileName("nature.jpg");
        mockPicture.setStatus(PictureStatus.PENDING);
        mockPicture.setUploadedBy(mockUser);
    }

    @Test
    void uploadPicture_ShouldSavePictureAsPending_WhenValidRequest() {

        MultipartFile mockFile = mock(MultipartFile.class);

        when(userRepository.findByEmail("ahmed@mail.com"))
                .thenReturn(Optional.of(mockUser));

        when(storageService.store(any(MultipartFile.class)))
                .thenReturn("saved_test_image.jpg");

        Path mockPath = Paths.get("mock/path/saved_test_image.jpg");

        when(storageService.load("saved_test_image.jpg"))
                .thenReturn(mockPath);

        when(pictureRepository.save(any(Picture.class)))
                .thenReturn(mockPicture);

        PictureResponse response =
                pictureService.uploadPicture(
                        mockFile,
                        "Nature Pic",
                        "ahmed@mail.com"
                );

        assertNotNull(response);

        verify(storageService).store(mockFile);

        verify(storageService)
                .load("saved_test_image.jpg");

        verify(pictureRepository)
                .save(any(Picture.class));
    }

    @Test
    void uploadPicture_ShouldThrowException_WhenUserNotFound() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                pictureService.uploadPicture(mockFile, "Title", "unknown@mail.com")
        );
        verify(pictureRepository, never()).save(any(Picture.class));
    }

    @Test
    void getAllApprovedPicturesForLanding_ShouldReturnOnlyApprovedPictures() {
        // Given
        mockPicture.setStatus(PictureStatus.APPROVED);
        when(pictureRepository.findByStatus(PictureStatus.APPROVED)).thenReturn(List.of(mockPicture));

        // When
        List<PictureResponse> result = pictureService.getAllApprovedPicturesForLanding();

        // Then
        assertEquals(1, result.size());
        verify(pictureRepository, times(1)).findByStatus(PictureStatus.APPROVED);
    }

    @Test
    void getPicturesByStatus_ShouldReturnMatchingPictures() {
        // Given
        when(pictureRepository.findByStatus(PictureStatus.PENDING)).thenReturn(List.of(mockPicture));

        // When
        List<PictureResponse> result = pictureService.getPicturesByStatus(PictureStatus.PENDING);

        // Then
        assertEquals(1, result.size());
        verify(pictureRepository, times(1)).findByStatus(PictureStatus.PENDING);
    }

    @Test
    void reviewPicture_ShouldUpdateStatusToApproved_WhenAdminApproves() {
        // Given
        when(pictureRepository.findById(100L)).thenReturn(Optional.of(mockPicture));
        when(pictureRepository.save(any(Picture.class))).thenReturn(mockPicture);

        // When
        PictureResponse response = pictureService.reviewPicture(100L, PictureStatus.APPROVED);

        // Then
        assertNotNull(response);
        assertEquals(PictureStatus.APPROVED, mockPicture.getStatus()); // تأكيد تغير الحالة
        verify(pictureRepository, times(1)).save(mockPicture);
    }

    @Test
    void reviewPicture_ShouldUpdateStatusToRejected_WhenAdminRejects() {
        // Given
        when(pictureRepository.findById(100L)).thenReturn(Optional.of(mockPicture));
        when(pictureRepository.save(any(Picture.class))).thenReturn(mockPicture);

        // When
        PictureResponse response = pictureService.reviewPicture(100L, PictureStatus.REJECTED);

        // Then
        assertNotNull(response);
        assertEquals(PictureStatus.REJECTED, mockPicture.getStatus());
        verify(pictureRepository, times(1)).save(mockPicture);
    }

    @Test
    void reviewPicture_ShouldThrowException_WhenPictureIdNotFound() {
        // Given
        when(pictureRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                pictureService.reviewPicture(999L, PictureStatus.APPROVED)
        );
        verify(pictureRepository, never()).save(any(Picture.class));
    }
}