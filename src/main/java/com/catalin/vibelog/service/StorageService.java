package com.catalin.vibelog.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Defines operations for storing, retrieving, and deleting binary file resources.
 * Implementations might store files on local filesystem, cloud storage, or other backends.
 */
public interface StorageService {

    /**
     * Store the given multipart file and return a publicly accessible URL or path.
     * Implementations are responsible for generating a unique storage location
     * and handling any I/O errors.
     *
     * @param file the {@link MultipartFile} to store
     * @return a {@link String} representing the public URL or storage path of the saved file
     */
    String store(MultipartFile file);

    /**
     * Delete the stored file located at the given public URL or path.
     * Implementations should verify existence and handle cascade cleanup if necessary.
     *
     * @param url the public URL or internal storage path of the file to delete
     */
    void delete(String url);

    /**
     * Load the file at the given URL or path as a Spring {@link Resource},
     * suitable for serving to clients (e.g., download endpoints).
     *
     * @param url the public URL or internal storage path of the file to load
     * @return a {@link Resource} representing the file's contents
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no file exists at the given location
     */
    Resource loadAsResource(String url);
}
