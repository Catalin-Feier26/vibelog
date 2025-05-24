import api from './axios';

export const toggleLike = postId => api.post(`/posts/${postId}/likes`);
export const getLikes   = postId => api.get(`/posts/${postId}/likes`);
