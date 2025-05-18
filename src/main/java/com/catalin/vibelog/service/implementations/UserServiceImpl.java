//package com.catalin.vibelog.service.implementations;
//
//import com.catalin.vibelog.DTOs.response.AuthResponse;
//import com.catalin.vibelog.DTOs.request.LoginRequest;
//import com.catalin.vibelog.DTOs.request.RegisterRequest;
//import com.catalin.vibelog.model.RegularUser;
//import com.catalin.vibelog.model.enums.Role;
//import com.catalin.vibelog.model.User;
//import com.catalin.vibelog.repository.UserRepository;
//import com.catalin.vibelog.security.JwtUtil;
//import com.catalin.vibelog.service.UserService;
//import com.catalin.vibelog.exception.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public User findUserById(long userId) {
//        return userRepository.findById(userId).orElse(null);
//    }
//
//    @Override
//    public User findUserByEmail(String email) {
//        return userRepository.findUserByEmail(email);
//    }
//
//    @Override
//    public AuthResponse register(RegisterRequest request) {
//        boolean exists = userRepository.findUserByEmail(request.getEmail()) != null
//                || userRepository.findUserByUsername(request.getUsername()) != null;
//
//        if (exists) {
//            throw new EmailAlreadyExistsException("User with this email or username already exists.");
//        }
//
//        RegularUser user = new RegularUser();
//        user.setEmail(request.getEmail());
//        user.setUsername(request.getUsername());
//        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
//        user.setRole(Role.USER);
//
//        User savedUser = userRepository.save(user);
//
//        String token = jwtUtil.generateToken(savedUser);
//
//        return new AuthResponse(token, savedUser.getEmail(), savedUser.getRole().toString());
//    }
//
//    @Override
//    public AuthResponse login(LoginRequest request) {
//        User user = userRepository.findUserByEmail(request.getEmail());
//
//        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
//            throw new InvalidCredentialsException("Invalid email or password");
//        }
//
//        String token = jwtUtil.generateToken(user);
//
//        return new AuthResponse(token, user.getEmail(), user.getRole().toString());
//    }
//}
