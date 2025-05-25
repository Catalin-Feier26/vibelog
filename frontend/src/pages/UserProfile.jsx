// src/pages/UserProfile.jsx
import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import {
    isFollowing,
    followUser,
    unfollowUser,
    getFollowCounts
} from '../api/followService';
import { getUserProfile } from '../api/userService';
import { getPublishedPostsByUser } from '../api/postService';
import PostCard from '../components/PostCard';
import './UserProfile.css';

export default function UserProfile() {
    const { username: other } = useParams();
    const { user: me }        = useContext(AuthContext);
    const meUsername          = me?.username;

    const [profile, setProfile]     = useState(null);
    const [following, setFollowing] = useState(false);
    const [counts, setCounts]       = useState({ followers: 0, following: 0 });
    const [posts, setPosts]         = useState([]);

    useEffect(() => {
        // 1. load profile info
        getUserProfile(other)
            .then(res => setProfile(res.data))
            .catch(console.error);

        // 2. load follow state + counts (only if logged in)
        if (meUsername) {
            isFollowing(meUsername, other)
                .then(setFollowing)
                .catch(console.error);

            getFollowCounts(meUsername, other)
                .then(setCounts)
                .catch(console.error);
        }

        // 3. load published posts
        getPublishedPostsByUser(other, 0, 15)
            .then(res => setPosts(res.data.content))
            .catch(console.error);
    }, [meUsername, other]);

    const toggleFollow = () => {
        const action = following ? unfollowUser : followUser;
        action(meUsername, other)
            .then(() => {
                // flip state
                setFollowing(f => !f);
                // and refresh counts
                return getFollowCounts(meUsername, other);
            })
            .then(setCounts)
            .catch(console.error);
    };

    if (!profile) {
        return <div className="profile-page">Loading…</div>;
    }

    return (
        <div className="profile-page">
            <div className="profile-header">
                <img
                    className="avatar"
                    src={profile.profilePicture || '/avatar-placeholder.png'}
                    alt={`${other}'s avatar`}
                />
                <div className="user-info">
                    <h2>@{profile.username}</h2>
                    <p className="bio">{profile.bio}</p>
                    {meUsername && (
                        <button onClick={toggleFollow} className="btn-follow">
                            {following ? 'Unfollow' : 'Follow'}
                        </button>
                    )}
                    <p className="counts">
                        <strong>{counts.followers}</strong> Followers ·{' '}
                        <strong>{counts.following}</strong> Following
                    </p>
                </div>
            </div>
            <hr />
            <div className="posts-list">
                {posts.map(p => (
                    <PostCard key={p.id} post={p} />
                ))}
            </div>
        </div>
    );
}
