package com.catalin.vibelog.model;

public enum Role {
    USER, MODERATOR, ADMIN;

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
