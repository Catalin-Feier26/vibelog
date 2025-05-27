import React from 'react';
import { Link } from 'react-router-dom';
import './UserCard.css';

export default function UserCard({ user }) {
    return (
        <Link to={`/users/${user.username}`} className="user-card animate-fadein">
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
