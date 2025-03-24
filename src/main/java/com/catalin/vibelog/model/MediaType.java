package com.catalin.vibelog.model;

public enum MediaType {
    IMG, VIDEO;

    public static MediaType fromString(String type) {
        return MediaType.valueOf(type.toUpperCase());
    }
}
