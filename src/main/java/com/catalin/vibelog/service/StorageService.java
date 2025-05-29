package com.catalin.vibelog.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /** Stores the file and returns its public URL/path. */
    String store(MultipartFile file);

    /** Deletes the file at the given URL/path. */
    void delete(String url);

    /** (Optional) Load as a Spring Resource for downloads. */
    Resource loadAsResource(String url);
}
