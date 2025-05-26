import React, { useContext }    from 'react';
import { Link, useNavigate }    from 'react-router-dom';
import { AuthContext }          from '../contexts/AuthContext';
import NotificationBell         from './NotificationBell';
import './Navbar.css';

export default function Navbar() {
    const { user, logout } = useContext(AuthContext);
    const nav = useNavigate();

    const handleLogout = () => {
        logout();
        nav('/login');
    };

    return (
        <nav className="navbar">
            <div className="nav-container">
                <Link to="/" className="logo">VibeLog</Link>

                {user && (
                    <ul className="nav-links">
                        <li><Link to="/">Home</Link></li>
                        <li><Link to="/profile">Profile</Link></li>
                        <li><Link to="/posts">Feed</Link></li>
                        <li><Link to="/posts/new">New Post</Link></li>
                        <li><Link to="/posts/drafts">My Drafts</Link></li>
                        <li><Link to="/reports/my">My Reports</Link></li>
                        {user.roles.includes('MODERATOR') && (
                            <li><Link to="/moderate">Moderation</Link></li>
                        )}
                        {user.roles.includes('ADMIN') && (
                            <li><Link to="/admin/users">Admin Panel</Link></li>
                        )}
                    </ul>
                )}

                <div className="auth-buttons">
                    {user ? (
                        <>
                            {/* 🔍 Search button */}
                            <button
                                className="btn-search"
                                onClick={() => nav('/search')}
                                aria-label="Search"
                            >
                                🔍
                            </button>

                            {/* 🔔 Notifications */}
                            <NotificationBell />

                            {/* Logout */}
                            <button onClick={handleLogout} className="logout-btn">
                                Logout
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login"    className="login-btn">Log In</Link>
                            <Link to="/register" className="register-btn">Register</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}
