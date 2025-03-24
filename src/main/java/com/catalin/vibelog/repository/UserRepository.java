package com.catalin.vibelog.repository;

import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    User findUserByEmailAndPasswordHash(String email, String passwordHash);
    User findUserByUsernameAndPasswordHash(String username, String passwordHash);
}
