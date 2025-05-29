package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;
import com.catalin.vibelog.exception.EmailAlreadyExistsException;
import com.catalin.vibelog.exception.ResourceNotFoundException;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.security.JwtUtil;
import com.catalin.vibelog.service.implementations.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getProfile_WhenUserExists_ReturnsProfileResponse() {
        Long userId = 1L;
        User user = new RegularUser();
        user.setId(userId);
        user.setEmail("email@example.com");
        user.setUsername("testuser");
        user.setBio("Test bio");
        user.setProfilePicture("pic-url");
        user.setRole(Role.USER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        ProfileResponse response = userService.getProfile(userId);

        // Record-style accessors, not getXxx()
        assertEquals(userId,            response.id());
        assertEquals("email@example.com", response.email());
        assertEquals("testuser",          response.username());
        assertEquals("Test bio",          response.bio());
        assertEquals("pic-url",           response.profilePicture());
        assertTrue(       response.roles().contains("USER"));
        verify(userRepo).findById(userId);
    }

    @Test
    void getProfile_WhenUserNotFound_ThrowsException() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(1L));
    }

    @Test
    void createUser_Success() {
        RegisterRequest req = new RegisterRequest("email@example.com", "newuser", "password");
        when(userRepo.existsByEmail("email@example.com")).thenReturn(false);
        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPwd");

        RegularUser savedUser = new RegularUser();
        savedUser.setId(2L);
        savedUser.setEmail("email@example.com");
        savedUser.setUsername("newuser");
        savedUser.setPasswordHash("hashedPwd");
        savedUser.setRole(Role.USER);
        when(userRepo.save(any(RegularUser.class))).thenReturn(savedUser);

        ProfileResponse response = userService.createUser(req);

        assertEquals(2L,                  response.id());
        assertEquals("email@example.com", response.email());
        assertEquals("newuser",           response.username());
        assertTrue(       response.roles().contains("USER"));
        verify(userRepo).existsByEmail("email@example.com");
        verify(userRepo).existsByUsername("newuser");
        verify(passwordEncoder).encode("password");
        verify(userRepo).save(any(RegularUser.class));
    }

    @Test
    void createUser_WhenEmailExists_ThrowsException() {
        RegisterRequest req = new RegisterRequest("dup@example.com", "anyuser", "pwd");
        when(userRepo.existsByEmail("dup@example.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(req));
        verify(userRepo).existsByEmail("dup@example.com");
    }

    @Test
    void updateProfileAndGetTokenByUsername_Success() {
        String username = "user1";
        ProfileUpdateRequest req = new ProfileUpdateRequest(null, "newName", null, null);
        User existing = new RegularUser();
        existing.setUsername(username);
        existing.setEmail("old@example.com");
        existing.setRole(Role.USER);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(existing));

        User saved = new RegularUser();
        saved.setUsername("newName");
        saved.setEmail("old@example.com");
        saved.setRole(Role.USER);
        when(userRepo.save(existing)).thenReturn(saved);
        when(jwtUtil.generateToken(saved)).thenReturn("new.token.value");

        ProfileUpdateWithTokenResponse result =
                userService.updateProfileAndGetTokenByUsername(username, req);

        assertEquals("new.token.value",      result.token());
        // ProfileResponse is a record too
        assertEquals("newName",              result.profile().username());
        verify(userRepo).findByUsername(username);
        verify(userRepo).save(existing);
        verify(jwtUtil).generateToken(saved);
    }


    @Test
    void deleteUserById_WhenExists_Deletes() {
        when(userRepo.existsById(5L)).thenReturn(true);
        userService.deleteUserById(5L);
        verify(userRepo).deleteById(5L);
    }

    @Test
    void deleteUserById_WhenNotExists_ThrowsException() {
        when(userRepo.existsById(5L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(5L));
    }

    @Test
    void listUsers_WithoutFragment_CallsFindAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> page = new PageImpl<>(List.of());
        when(userRepo.findAll(pageable)).thenReturn(page);

        Page<ProfileResponse> result = userService.listUsers(null, pageable);

        assertEquals(0, result.getTotalElements());
        verify(userRepo).findAll(pageable);
    }
}
