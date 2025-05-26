// src/pages/AdminUsersPage.jsx
import React, { useEffect, useState } from 'react';
import {
    getUsers,
    createUser,
    updateUser,
    deleteUser,
    getTopLikedPost,
    getTopCommentedPost,
    getTopRebloggedPost
} from '../api/adminService';
import PostCard from '../components/PostCard';
import './AdminUsersPage.css';

export default function AdminUsersPage() {
    // — Users CRUD state —
    const [users, setUsers]         = useState([]);
    const [loading, setLoading]     = useState(true);
    const [error, setError]         = useState('');
    const [page, setPage]           = useState(0);
    const [size]                    = useState(50);
    const [search, setSearch]       = useState('');
    const [newUser, setNewUser]     = useState({ email: '', username: '', password: '' });
    const [editingId, setEditingId] = useState(null);
    const [editData, setEditData]   = useState({ email: '', username: '' });

    // — Analytics state —
    const [topLiked, setTopLiked]           = useState(null);
    const [topCommented, setTopCommented]   = useState(null);
    const [topReblogged, setTopReblogged]   = useState(null);

    // load users + analytics
    const loadAll = async () => {
        setError('');
        setLoading(true);
        try {
            // users
            const u = await getUsers(search, page, size);
            setUsers(u.data.content);
            // analytics
            const [liked, commented, reblogged] = await Promise.all([
                getTopLikedPost().catch(() => ({ data: null })),
                getTopCommentedPost().catch(() => ({ data: null })),
                getTopRebloggedPost().catch(() => ({ data: null }))
            ]);
            setTopLiked(liked.data);
            setTopCommented(commented.data);
            setTopReblogged(reblogged.data);
        } catch (e) {
            console.error(e);
            setError('Could not fetch data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadAll(); }, [page, search]);

    // user‐CRUD handlers (unchanged from before) …
    const handleCreate = async e => {
        e.preventDefault();
        try {
            await createUser(newUser);
            setNewUser({ email: '', username: '', password: '' });
            loadAll();
        } catch {
            alert('Failed to create user');
        }
    };
    const handleStartEdit = u => {
        setEditingId(u.id);
        setEditData({ email: u.email, username: u.username });
    };
    const handleSaveEdit = async id => {
        try {
            await updateUser(id, editData);
            setEditingId(null);
            loadAll();
        } catch {
            alert('Failed to update user');
        }
    };
    const handleCancelEdit = () => setEditingId(null);
    const handleDelete = async id => {
        if (!window.confirm(`Really delete user #${id}?`)) return;
        try {
            await deleteUser(id);
            loadAll();
        } catch {
            alert('Failed to delete user');
        }
    };

    return (
        <div className="admin-users-page">
            <h1>Admin Panel</h1>

            {/* ——— Analytics Section ——— */}
            <section className="analytics">
                <div className="analytics-box">
                    <h2>Most Liked Post</h2>
                    {topLiked
                        ? <PostCard post={topLiked} />
                        : <p>No post matches that.</p>
                    }
                </div>
                <div className="analytics-box">
                    <h2>Most Commented Post</h2>
                    {topCommented
                        ? <PostCard post={topCommented} />
                        : <p>No post matches that.</p>
                    }
                </div>
                <div className="analytics-box">
                    <h2>Most Reblogged Post</h2>
                    {topReblogged
                        ? <PostCard post={topReblogged} />
                        : <p>No post matches that.</p>
                    }
                </div>
            </section>

            {/* ——— New‐user form ——— */}
            <form className="new-user-form" onSubmit={handleCreate}>
                <h2>Create new user</h2>
                <input
                    type="text"
                    placeholder="Search by username…"
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
                <input
                    type="email"
                    placeholder="Email"
                    value={newUser.email}
                    onChange={e => setNewUser(u => ({ ...u, email: e.target.value }))}
                    required
                />
                <input
                    type="text"
                    placeholder="Username"
                    value={newUser.username}
                    onChange={e => setNewUser(u => ({ ...u, username: e.target.value }))}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={newUser.password}
                    onChange={e => setNewUser(u => ({ ...u, password: e.target.value }))}
                    required
                />
                <button type="submit">Create</button>
            </form>

            {error && <div className="error">{error}</div>}
            {loading ? (
                <div>Loading…</div>
            ) : (
                <table className="users-table">
                    <thead>
                    <tr>
                        <th>ID</th><th>Email</th><th>Username</th><th>Roles</th><th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map(u => {
                        const isEditing = editingId === u.id;
                        return (
                            <tr key={u.id}>
                                <td>{u.id}</td>
                                <td>
                                    {isEditing
                                        ? <input
                                            value={editData.email}
                                            onChange={e => setEditData(d => ({ ...d, email: e.target.value }))}
                                        />
                                        : u.email
                                    }
                                </td>
                                <td>
                                    {isEditing
                                        ? <input
                                            value={editData.username}
                                            onChange={e => setEditData(d => ({ ...d, username: e.target.value }))}
                                        />
                                        : u.username
                                    }
                                </td>
                                <td>{u.roles.join(', ')}</td>
                                <td className="actions">
                                    {isEditing ? (
                                        <>
                                            <button onClick={() => handleSaveEdit(u.id)}>Save</button>
                                            <button onClick={handleCancelEdit}>Cancel</button>
                                        </>
                                    ) : (
                                        <>
                                            <button onClick={() => handleStartEdit(u)}>Edit</button>
                                            <button onClick={() => handleDelete(u.id)}>Delete</button>
                                        </>
                                    )}
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
}
