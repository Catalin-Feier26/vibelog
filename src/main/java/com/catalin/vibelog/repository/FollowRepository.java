package com.catalin.vibelog.repository;

import model.FollowId;
import model.Follow;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow,FollowId>{
    List<Follow> findByFollowingUser_Id(Long userId);
    List<Follow> findByFollowedUser_Id(Long userId);
    boolean existsByFollowingUserAndFollowedUser(User follower, User followed);
}
