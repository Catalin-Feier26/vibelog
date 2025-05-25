import api from './axios';

/**
 * Submit a report on a post.
 * @param {number} postId
 * @param {string} reason
 */
export const reportPost = (postId, reason) =>
    api.post('/reports', { postId, reason });

/**
 * Submit a report on a comment.
 * @param {number} commentId
 * @param {string} reason
 */
export const reportComment = (commentId, reason) =>
    api.post('/reports', { commentId, reason });

/**
 * Fetch the current user’s own reports.
 */
export const getMyReports = () =>
    api.get('/reports/my');
