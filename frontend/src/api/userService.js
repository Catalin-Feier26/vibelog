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
