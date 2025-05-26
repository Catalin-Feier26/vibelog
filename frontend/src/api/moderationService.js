import api from './axios';

/**
 * Delete a post as a moderator (clears all reports first in the back end).
 * @param {number} postId
 */
export const deletePostAsMod = postId =>
    api.delete(`/moderation/posts/${postId}`);

/**
 * Delete a comment as a moderator (clears all reports first in the back end).
 * @param {number} commentId
 */
export const deleteCommentAsMod = commentId =>
    api.delete(`/moderation/comments/${commentId}`);
