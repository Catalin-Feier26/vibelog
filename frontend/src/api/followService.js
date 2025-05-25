import api from './axios';

/**
 * Check if `follower` is following `followee`.
 * @param {string} follower  username of the follower
 * @param {string} followee  username of the followee
 * @returns {Promise<boolean>}
 */
export const isFollowing = (follower, followee) =>
    api
        .post('/follows/state', { followerUsername: follower, followeeUsername: followee })
        .then(res => res.data.following);

/**
 * Make `follower` follow `followee`.
 * @param {string} follower  username of the follower
 * @param {string} followee  username of the followee
 * @returns {Promise}
 */
export const followUser = (follower, followee) =>
    api.post('/follows', { followerUsername: follower, followeeUsername: followee });

/**
 * Make `follower` unfollow `followee`.
 * @param {string} follower  username of the follower
 * @param {string} followee  username of the followee
 * @returns {Promise}
 */
export const unfollowUser = (follower, followee) =>
    api.delete('/follows', {
        data: { followerUsername: follower, followeeUsername: followee }
    });

/**
 * Get follower/following counts for `username`.
 * @param {string} username
 * @returns {Promise<{followers: number, following: number}>}
 */
export const getFollowCounts = (username) =>
    api
        .post('/follows/count', { followerUsername: '', followeeUsername: username })
        .then(res => res.data);
