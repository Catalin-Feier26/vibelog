package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.service.AdminAnalyticsService;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final PostRepository postRepo;
    private final PostService    postService;

    public AdminAnalyticsServiceImpl(PostRepository postRepo,
                                     PostService postService) {
        this.postRepo    = postRepo;
        this.postService = postService;
    }

    private PostResponse fetchFirstOrNull(List<Long> ids) {
        if (ids.isEmpty()) return null;
        return postService.getPostById(ids.get(0));
    }

    @Override
    public PostResponse topLikedPost() {
        List<Long> ids = postRepo.findTopLikedPostIds(PageRequest.of(0,1));
        return fetchFirstOrNull(ids);
    }

    @Override
    public PostResponse topCommentedPost() {
        List<Long> ids = postRepo.findTopCommentedPostIds(PageRequest.of(0,1));
        return fetchFirstOrNull(ids);
    }

    @Override
    public PostResponse topRebloggedPost() {
        List<Long> ids = postRepo.findTopRebloggedPostIds(PageRequest.of(0,1));
        return fetchFirstOrNull(ids);
    }
}