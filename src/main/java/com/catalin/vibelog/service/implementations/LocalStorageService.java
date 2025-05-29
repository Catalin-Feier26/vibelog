package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.service.StorageService;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;

/**
 * Local filesystem implementation of {@link StorageService}.
 * <p>
 * Stores uploaded files under a configurable directory and
 * provides methods to generate public URLs, delete files, and
 * load files as Spring {@link UrlResource}s for download.
 * </p>
 */
@Service
public class LocalStorageService implements StorageService {

    /**
     * Root directory for storing files.
     */
    private final Path root = Paths.get("uploads");

    /**
     * Initializes the storage directory, creating it if necessary.
     *
     * @throws IOException if the directory could not be created
     */
    public LocalStorageService() throws IOException {
        Files.createDirectories(root);
    }

    /**
     * Store the given multipart file on the local filesystem and return
     * a public URL constructed for accessing it via a resource handler.
     *
     * @param file the {@link MultipartFile} to store
     * @return a {@link String} representing the access URL for the stored file
     * @throws RuntimeException if an I/O error occurs during storage
     */
    @Override
    public String store(MultipartFile file) {
        try {
            // Clean the incoming filename to prevent path traversal
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = System.currentTimeMillis() + "_" + original;

            // Save to disk
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Build and return a full URL pointing at our /uploads/** resource handler
            return ServletUriComponentsBuilder.fromCurrentContextPath()   // e.g. http://localhost:8080
                    .path("/uploads/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Delete the file referenced by the given URL from local storage.
     * Will quietly swallow exceptions to avoid impacting caller.
     *
     * @param url the public or internal URL pointing to the stored file
     */
    @Override
    public void delete(String url) {
        try {
            // Extract just the filename portion from the URL
            String filename = Paths.get(URI.create(url).getPath()).getFileName().toString();
            Files.deleteIfExists(root.resolve(filename));
        } catch (Exception e) {
            // log and swallow
        }
    }

    /**
     * Load the file at the given URL into a Spring {@link UrlResource},
     * suitable for serving to clients (e.g., download endpoints).
     *
     * @param url the public or internal URL of the file
     * @return a {@link UrlResource} representing the file
     * @throws RuntimeException if the file cannot be found or the URL is invalid
     */
    @Override
    public UrlResource loadAsResource(String url) {
        try {
            String filename = Paths.get(URI.create(url).getPath()).getFileName().toString();
            Path file = root.resolve(filename);
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }
}
