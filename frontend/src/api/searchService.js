import api from './axios';

/**
 * Search users by username (contains, case-insensitive).
 * Returns a Spring-Page of { id, username, bio, profilePicture }.
 */
export const searchUsers = (q, page = 0, size = 15) =>
    api.get('/search/users', { params: { q, page, size } });

/**
 * Search posts by title OR body (published only).
 * Returns a Spring-Page of PostResponse DTO.
 */
export const searchPosts = (q, page = 0, size = 15) =>
    api.get('/search/posts', { params: { q, page, size } });
