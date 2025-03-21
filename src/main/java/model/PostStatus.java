package model;

public enum PostStatus {
    DRAFT, PUBLISHED;

    public static PostStatus fromString(String status) {
        return PostStatus.valueOf(status.toUpperCase());
    }
}
