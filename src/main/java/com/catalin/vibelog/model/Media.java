package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a media resource (image or video) that can be attached to posts.
 */
@Getter
@Setter
@Entity
@Table(name = "media")
public class Media {

    /** Primary key identifier for the media. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** The type of media (e.g., IMG or VIDEO). */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType type;

    /** File format or extension (e.g., "jpg", "mp4"). */
    @Column(nullable = false)
    private String format;

    /** Size of the media file in bytes. */
    @Column(nullable = false)
    private long size;

    /** URL or path where the media is stored. */
    @Column(nullable = false)
    private String url;

    /**
     * All posts that reference this media.
     * Mapped by the {@code mediaList} field in {@link Post}.
     */
    @ManyToMany(mappedBy = "mediaList")
    private List<Post> posts = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Media() {
    }
}
