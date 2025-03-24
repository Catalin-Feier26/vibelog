package com.catalin.vibelog.service;

import model.Post;

import java.util.List;

public interface PostService {
    Post createPost(Post post, Long userId);
    List<Post> getPostsByUserId(Long userId);
    Post getPostById(Long postId);
    List<Post> searchPosts(String title);
}
