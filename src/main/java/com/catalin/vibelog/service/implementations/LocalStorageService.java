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

@Service
public class LocalStorageService implements StorageService {

    private final Path root = Paths.get("uploads");

    public LocalStorageService() throws IOException {
        Files.createDirectories(root);
    }

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
