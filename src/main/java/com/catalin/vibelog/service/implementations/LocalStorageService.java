package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.service.StorageService;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;  // or however your static mapping is set up
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String url) {
        try {
            Path file = root.resolve(Paths.get(url).getFileName());
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // log, but don’t fail the whole app
        }
    }

    @Override
    public UrlResource loadAsResource(String url) {
        try {
            Path file = root.resolve(Paths.get(url).getFileName());
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }
}
