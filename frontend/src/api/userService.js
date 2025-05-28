import api from './axios';

/**
 * Fetch current user's profile.
 */
export const getMyProfile = () => api.get('/users/me');

/**
 * Update current user's profile.
 * Returns an object { token, profile } where:
 *  - token is the new JWT
 *  - profile is the updated ProfileResponse
 */
export const updateMyProfile = async (data) => {
    const resp = await api.put('/users/me', data);
    return resp.data;
};
/**
 * Fetch any user’s profile by username.
 * @param {string} username
 * @returns {Promise<ProfileResponse>}
 */
export const getUserProfile = (username) =>
    api.get(`/users/${username}`);

export const uploadAvatar = file => {
    const form = new FormData();
    form.append('file', file);
    return api
        .post('/users/me/avatar', form, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => res.data);
};

export const deleteAvatar = () =>
    api.delete('/users/me/avatar');