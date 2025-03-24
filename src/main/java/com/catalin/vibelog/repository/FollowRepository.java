package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.FollowId;
import com.catalin.vibelog.model.Follow;
import com.catalin.vibelog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,FollowId>{
    List<Follow> findByFollowingUser_Id(Long userId);
    List<Follow> findByFollowedUser_Id(Long userId);
    boolean existsByFollowingUserAndFollowedUser(User follower, User followed);
}
