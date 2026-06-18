package com.ahmed.picturepublishingservice.picture.service;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Storage initialized successfully at: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed.");
            }

            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new SecurityException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("File stored successfully: {}", uniqueFilename);
                return uniqueFilename;
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
            log.info("File deleted successfully from disk: {}", filename);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }
}