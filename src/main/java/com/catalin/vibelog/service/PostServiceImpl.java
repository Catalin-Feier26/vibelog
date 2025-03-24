package com.catalin.vibelog.service;

import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.PostStatus;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post createPost(Post post, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.setUser(user);
        post.setStatus(PostStatus.PUBLISHED);

        // Save post first
        Post savedPost = postRepository.save(post);


        if (user instanceof RegularUser regular) {
            regular.setPostCount(regular.getPostCount() + 1);
            userRepository.save(regular);
        }

        return savedPost;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId){
        return postRepository.findAllByUser_Id(userId);
    }

    @Override
    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public List<Post> searchPosts(String title){
        return postRepository.findByTitleContaining(title);
    }
}
