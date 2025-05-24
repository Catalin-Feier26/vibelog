import api from './axios';

export const getCommentsForPost = postId =>
    api.get(`/posts/${postId}/comments`);

export const addComment = (postId, data) =>
    api.post(`/posts/${postId}/comments`, data);

export const deleteComment = id =>
    api.delete(`/comments/${id}`);