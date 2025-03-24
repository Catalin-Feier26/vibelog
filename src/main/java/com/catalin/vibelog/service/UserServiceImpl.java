package com.catalin.vibelog.service;

import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User registerUser(User user) {
        boolean exists = userRepository.findUserByEmail(user.getEmail()) != null
                || userRepository.findUserByUsername(user.getUsername()) != null;

        if (exists) {
            throw new RuntimeException("User with this email or username already exists.");
        }

        return userRepository.save(user);
    }


    @Override
    public User login(String email, String passwordHash) {
        User user = userRepository.findUserByEmail(email);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            return user;
        }
        return null;
    }

}
