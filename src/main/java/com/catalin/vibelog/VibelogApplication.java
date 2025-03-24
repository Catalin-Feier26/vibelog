package com.catalin.vibelog;

import com.catalin.vibelog.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.catalin.vibelog.service.UserService;
import com.catalin.vibelog.service.PostService;

@SpringBootApplication
public class VibelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(VibelogApplication.class, args);

    }
    @Bean
    public CommandLineRunner testPostService(PostService postService, UserService userService) {
        return args -> {
            // 1. Register a test user (only if not already in DB)
            User user = new RegularUser();
            user.setEmail("author@example.com");
            user.setUsername("author123");
            user.setPasswordHash("password");
            user.setRole(Role.USER);

            try {
                ((RegularUser) user).setPostCount(0);
                userService.registerUser(user);
                System.out.println("✅ Test user registered");
            } catch (Exception e) {
                System.out.println("⚠️  User might already exist: " + e.getMessage());
                user = userService.findUserByEmail("author@example.com");
            }


            Post post = new Post();
            post.setTitle("My First Vibe");
            post.setBody("This is my first post using PostService!");
            post.setStatus(PostStatus.PUBLISHED);

            Post savedPost = postService.createPost(post, user.getId());
            System.out.println("📝 Post created: " + savedPost.getTitle());

            // 3. Fetch all posts by user
            System.out.println("📥 All posts by user:");
            postService.getPostsByUserId(user.getId())
                    .forEach(p -> System.out.println(" - " + p.getTitle()));
        };
    }
}
