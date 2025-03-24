package com.catalin.vibelog.service;

import model.Post;
import model.User;
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
    public Post createPost(Post post, Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return null;
        }
        post.setUser(user);
        return postRepository.save(post);
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
