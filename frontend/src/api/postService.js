import api from './axios';

export const getAllPosts    = () => api.get('/posts');
export const getPostById    = id => api.get(`/posts/${id}`);
export const createPost     = data => api.post('/posts', data);
export const updatePost     = (id, data) => api.put(`/posts/${id}`, data);
export const deletePost     = id => api.delete(`/posts/${id}`);
export const getMyDrafts = () => api.get('/posts/me/drafts');

/**
 * Get all “PUBLISHED” posts by a given user, paged.
 * @param {string} username
 * @param {number} page    zero-based page index (default 0)
 * @param {number} size    page size (default 15)
 * @returns {Promise<Page<PostResponse>>}
 */
export const getPublishedPostsByUser = (username, page = 0, size = 15) =>
    api.get(`/posts/user/${username}`, {params: { page, size }});

export const getReblogState = postId =>
    api.get(`/posts/${postId}/reblog/state`).then(res => res.data.reblogged);

export const getReblogCount = postId =>
    api.get(`/posts/${postId}/reblog/count`).then(res => res.data.count);

export const reblogPost = postId =>
    api.post(`/posts/${postId}/reblog`);

export const undoReblogPost = postId =>
    api.delete(`/posts/${postId}/reblog`);