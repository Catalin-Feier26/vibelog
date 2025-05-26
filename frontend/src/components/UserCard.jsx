// src/components/UserCard.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './UserCard.css';

/**
 * A simple card showing a user’s avatar, username and bio,
 * and linking to their profile page.
 *
 * @param {{
 *   user: {
 *     username: string,
 *     profilePicture?: string,
 *     bio?: string
 *   }
 * }} props
 */
export default function UserCard({ user }) {
    return (
        <Link to={`/users/${user.username}`} className="user-card">
            <img
                className="user-card-avatar"
                src={user.profilePicture || '/avatar-placeholder.png'}
                alt={`@${user.username}’s avatar`}
            />
            <div className="user-card-info">
                <strong className="user-card-username">@{user.username}</strong>
                {user.bio && <p className="user-card-bio">{user.bio}</p>}
            </div>
        </Link>
    );
}
