import api from './axios';

/**
 * Fetch a page of notifications for the current user.
 * @param {number} page zero-based page index
 * @param {number} size page size
 */
export const getNotifications = (page = 0, size = 10) =>
    api.get('/notifications', { params: { page, size } });

/** Fetch the count of unread notifications. */
export const getNotificationCount = () =>
    api.get('/notifications/count');

/** Mark all notifications as read. */
export const markAllNotificationsRead = () =>
    api.put('/notifications/read');

/** Mark a single notification as read. */
export const markNotificationRead = id =>
    api.put(`/notifications/${id}/read`);
