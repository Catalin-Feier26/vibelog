package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.service.AdminAnalyticsService;
import com.catalin.vibelog.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link AdminAnalyticsService}, providing analytics
 * on posts by engagement metrics.
 * <p>
 * Retrieves the top-performing posts by likes, comments, and reblogs.
 * </p>
 */
@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final PostRepository postRepo;
    private final PostService postService;

    /**
     * Constructs the analytics service with required dependencies.
     *
     * @param postRepo    repository for querying post engagement metrics
     * @param postService service for retrieving full post details
     */
    public AdminAnalyticsServiceImpl(PostRepository postRepo,
                                     PostService postService) {
        this.postRepo = postRepo;
        this.postService = postService;
    }

    /**
     * Helper to fetch the first post by ID or return null if none.
     *
     * @param ids list of post IDs, expecting the first as top result
     * @return the {@link PostResponse} for the first ID, or {@code null} if list is empty
     */
    private PostResponse fetchFirstOrNull(List<Long> ids) {
        if (ids.isEmpty()) {
            return null;
        }
        return postService.getPostById(ids.get(0));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the most-liked post overall.
     * </p>
     */
    @Override
    public PostResponse topLikedPost() {
        List<Long> ids = postRepo.findTopLikedPostIds(PageRequest.of(0, 1));
        return fetchFirstOrNull(ids);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the most-commented post overall.
     * </p>
     */
    @Override
    public PostResponse topCommentedPost() {
        List<Long> ids = postRepo.findTopCommentedPostIds(PageRequest.of(0, 1));
        return fetchFirstOrNull(ids);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves the most-reblogged post overall.
     * </p>
     */
    @Override
    public PostResponse topRebloggedPost() {
        List<Long> ids = postRepo.findTopRebloggedPostIds(PageRequest.of(0, 1));
        return fetchFirstOrNull(ids);
    }
}
