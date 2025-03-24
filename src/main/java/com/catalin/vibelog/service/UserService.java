package com.catalin.vibelog.service;

import com.catalin.vibelog.model.User;

public interface UserService {
     User findUserById(long userId);
     User findUserByEmail(String email);
     User registerUser(User user);
     User login(String email, String passwordHash);
}
