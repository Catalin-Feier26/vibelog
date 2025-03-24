package com.catalin.vibelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import model.RegularUser;
import model.Role;
import model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.catalin.vibelog.service.UserService;


@SpringBootApplication
public class VibelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibelogApplication.class, args);

    }
    @Bean
    public CommandLineRunner testUserService(UserService userService) {
        return args -> {
            // ✅ Create and register a new user
            RegularUser newUser = new RegularUser();
            newUser.setEmail("test@example.com");
            newUser.setUsername("testuser");
            newUser.setPasswordHash("hashed123");
            newUser.setRole(Role.USER); // Or ADMIN, MODERATOR

            User savedUser = userService.registerUser(newUser);
            System.out.println("✅ Registered user: " + savedUser.getUsername());

            // ✅ Retrieve by email
            User retrieved = userService.findUserByEmail("test@example.com");
            System.out.println("📥 Retrieved user: " + retrieved.getUsername());

            // ✅ Test login
            User loggedIn = userService.login("test@example.com", "hashed123");
            System.out.println("🔐 Login successful? " + loggedIn.getUsername() + " " + loggedIn.getId());
        };
    }
}
