package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name="follows")
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne
    @MapsId("followingUserId")
    @JoinColumn(name = "following_user_id", nullable = false)
    private User followingUser;

    @ManyToOne
    @MapsId("followedUserId")
    @JoinColumn(name = "followed_user_id", nullable = false)
    private User followedUser;

    @Column (name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Follow() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
