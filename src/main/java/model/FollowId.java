package model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class FollowId{
    private Long followingUserId;
    private Long followedUserId;

    public FollowId() {
    }

    public FollowId(Long followingUserId, Long followedUserId) {
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
    }
}
