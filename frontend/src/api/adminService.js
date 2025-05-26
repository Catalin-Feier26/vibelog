import api from './axios';

/**
 * Fetch a paginated list of all users, optionally filtering by username fragment.
 * @param {string} [search] substring to match usernames
 * @param {number} [page]   zero-based page index
 * @param {number} [size]   page size
 */
export const getUsers = (search = '', page = 0, size = 50) =>
    api.get('/admin/users', {
        params: { search: search || undefined, page, size }
    });

/**
 * Create a new user (admin only).
 * @param {{ email: string, username: string, password: string }} data
 */
export const createUser = data =>
    api.post('/admin/users', data);

/**
 * Update an existing user by ID.
 * @param {number|string} id
 * @param {{ email?: string, username?: string, bio?: string, profilePicture?: string }} data
 */
export const updateUser = (id, data) =>
    api.put(`/admin/users/${id}`, data);

/**
 * Delete a user by ID.
 * @param {number|string} id
 */
export const deleteUser = id =>
    api.delete(`/admin/users/${id}`);

/**
 * Fetch the single most-liked post (returns PostResponse or null).
 */
export const getTopLikedPost = () =>
    api.get('/admin/analytics/top-liked');

/**
 * Fetch the single most-commented post (returns PostResponse or null).
 */
export const getTopCommentedPost = () =>
    api.get('/admin/analytics/top-commented');

/**
 * Fetch the single most-reblogged post (returns PostResponse or null).
 */
export const getTopRebloggedPost = () =>
    api.get('/admin/analytics/top-reblogged');