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

/**
 * Fetch a page of reports filtered by status.
 * @param {string} status  e.g. 'PENDING', 'REVIEWED', 'RESOLVED'
 * @param {number} page    zero-based page index
 * @param {number} size    page size
 */
export const getReportsByStatus = (status = 'PENDING', page = 0, size = 15) =>
    api.get('/reports', { params: { status, page, size } });

/**
 * Update the status of a single report.
 * @param {number} reportId
 * @param {string} newStatus  one of 'PENDING'|'REVIEWED'|'RESOLVED'
 */
export const updateReportStatus = (reportId, newStatus) =>
    api.put(`/reports/${reportId}/status`, { status: newStatus });