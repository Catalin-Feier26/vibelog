import React, { useState, useEffect, useContext } from 'react';
import './Profile.css';
import { AuthContext } from '../contexts/AuthContext';
import { getMyProfile, updateMyProfile } from '../api/userService';

export default function Profile() {
    const { user, setUser, setToken } = useContext(AuthContext);

    const [form, setForm] = useState({
        email: '',
        username: '',
        bio: '',
        profilePicture: '',
        coverPhoto: ''
    });
    const [loading, setLoading]   = useState(true);
    const [error, setError]       = useState('');
    const [success, setSuccess]   = useState('');

    /* ─────────────────────────────────────────
       Fetch my profile once on mount
    ────────────────────────────────────────── */
    useEffect(() => {
        getMyProfile()
            .then(res => {
                const d = res.data;
                setForm({
                    email:          d.email  ?? '',
                    username:       d.username ?? '',
                    bio:            d.bio ?? '',
                    profilePicture: d.profilePicture ?? '',
                    coverPhoto:     d.coverPhoto ?? ''      // new!
                });
            })
            .catch(() => setError('Failed to load profile'))
            .finally(() => setLoading(false));
    }, []);

    /* ─────────────────────────────────────────
       Handlers
    ────────────────────────────────────────── */
    const handleChange = e =>
        setForm(f => ({ ...f, [e.target.name]: e.target.value }));

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');
        setSuccess('');
        try {
            const { token, profile } = await updateMyProfile(form);

            // swap JWT + update user context
            setToken(token);
            const newUser = { ...user, username: profile.username, email: profile.email, roles: profile.roles };
            setUser(newUser);
            sessionStorage.setItem('user', JSON.stringify(newUser));

            setSuccess('Profile updated!');
        } catch (err) {
            setError(err.response?.data?.message || 'Update failed');
        }
    };

    if (loading) return <div className="profile-loading">Loading…</div>;

    const avatarSrc =
        form.profilePicture ||
        `https://ui-avatars.com/api/?name=${encodeURIComponent(form.username || 'User')}&size=256&background=random`;

    /* ─────────────────────────────────────────
       JSX
    ────────────────────────────────────────── */
    return (
        <div className="profile-page">
            {/* Hero / cover image */}
            <div
                className="cover-photo"
                style={{
                    backgroundImage: `url(${
                        form.coverPhoto ||
                        'https://images.unsplash.com/photo-1503264116251-35a269479413?auto=format&fit=crop&w=1500&q=80'
                    })`
                }}
            />

            {/* Floating card */}
            <div className="profile-card">
                <div className="avatar-wrapper">
                    <img src={avatarSrc} alt="avatar" className="avatar" />
                </div>

                <h2 className="display-name">@{form.username}</h2>
                {form.bio && <p className="bio-text">{form.bio}</p>}

                {error && !success && <div className="error-msg">{error}</div>}
                {success && <div className="success-msg">{success}</div>}

                <form className="profile-form" onSubmit={handleSubmit}>
                    <h3>Edit profile</h3>

                    <div className="form-grid">
                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <input id="email" type="email" name="email" value={form.email} onChange={handleChange} required />
                        </div>

                        <div className="form-group">
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                type="text"
                                name="username"
                                value={form.username}
                                onChange={handleChange}
                                minLength={3}
                                maxLength={50}
                                required
                            />
                        </div>

                        <div className="form-group bio">
                            <label htmlFor="bio">Bio</label>
                            <textarea id="bio" name="bio" value={form.bio} onChange={handleChange} maxLength={500} />
                        </div>

                        <div className="form-group">
                            <label htmlFor="profilePicture">Profile picture URL</label>
                            <input
                                id="profilePicture"
                                type="text"
                                name="profilePicture"
                                value={form.profilePicture}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="coverPhoto">Cover photo URL</label>
                            <input
                                id="coverPhoto"
                                type="text"
                                name="coverPhoto"
                                value={form.coverPhoto}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <button type="submit" className="profile-submit">
                        Save changes
                    </button>
                </form>
            </div>
        </div>
    );
}
