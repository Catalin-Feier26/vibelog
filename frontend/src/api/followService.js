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
 * @param {string} follower  current user’s username
 * @param {string} followee  target user’s username
 */
export const getFollowCounts = (follower, followee) =>
    api.post('/follows/count', {
        followerUsername: follower,
        followeeUsername: followee
    })
        .then(res => res.data);
