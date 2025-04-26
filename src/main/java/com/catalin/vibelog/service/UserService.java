package com.catalin.vibelog.service;

import com.catalin.vibelog.DTOs.request.LoginRequest;
import com.catalin.vibelog.DTOs.request.RegisterRequest;
import com.catalin.vibelog.DTOs.response.AuthResponse;
import com.catalin.vibelog.model.User;

public interface UserService {
     User findUserById(long userId);
     User findUserByEmail(String email);
     AuthResponse register(RegisterRequest request);
     AuthResponse login(LoginRequest request);

}
