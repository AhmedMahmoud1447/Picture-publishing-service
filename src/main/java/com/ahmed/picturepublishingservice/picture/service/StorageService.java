package com.ahmed.picturepublishingservice.picture.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Service interface for handling physical file storage operations.
 * Provides abstraction for storing, loading, and deleting files
 * independently of the underlying storage provider (e.g., Local File System, AWS S3).
 *
 * @author ahmed
 * @since 2026-06
 */
public interface StorageService {

    /**
     * Stores a multi-part file into the configured storage location.
     * Generates a unique filename to prevent overwriting existing files.
     *
     * @param file the {@link MultipartFile} uploaded from the client-side
     * @return {@code String} the unique generated filename under which the file is saved
     * @throws IllegalArgumentException if the file is empty or the content type is not an image
     * @throws SecurityException        if a path traversal attack attempt is detected
     * @throws RuntimeException         if an I/O error occurs during file storage
     */
    String store(MultipartFile file);

    /**
     * Resolves the physical path of a given filename within the storage location.
     *
     * @param filename the unique name of the file to load
     * @return {@link Path} the absolute or relative path representing the file location
     */
    Path load(String filename);

    /**
     * Deletes the physical file from the storage location if it exists.
     *
     * @param filename the unique name of the file to be deleted
     * @throws RuntimeException if an I/O error occurs during file deletion
     */
    void delete(String filename);
}