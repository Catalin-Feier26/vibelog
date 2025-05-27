// AdminUsersPage.jsx
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
    const [users, setUsers]         = useState([]);
    const [loading, setLoading]     = useState(true);
    const [error, setError]         = useState('');
    const [page, setPage]           = useState(0);
    const [size]                    = useState(50);
    const [search, setSearch]       = useState('');
    const [newUser, setNewUser]     = useState({ email: '', username: '', password: '' });
    const [editingId, setEditingId] = useState(null);
    const [editData, setEditData]   = useState({ email: '', username: '' });

    const [topLiked, setTopLiked]         = useState(null);
    const [topCommented, setTopCommented] = useState(null);
    const [topReblogged, setTopReblogged] = useState(null);

    const loadAll = async () => {
        setError('');
        setLoading(true);
        try {
            const u = await getUsers(search, page, size);
            setUsers(u.data.content);
            const [liked, commented, reblogged] = await Promise.all([
                getTopLikedPost().catch(() => ({ data: null })),
                getTopCommentedPost().catch(() => ({ data: null })),
                getTopRebloggedPost().catch(() => ({ data: null }))
            ]);
            setTopLiked(liked.data);
            setTopCommented(commented.data);
            setTopReblogged(reblogged.data);
        } catch {
            setError('Could not fetch data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadAll(); }, [page, search]);

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
            <h1 className="page-title">Admin Panel</h1>

            <section className="analytics animate-fadein">
                <div className="analytics-box animate-fadeup">
                    <h2>Most Liked Post</h2>
                    {topLiked ? <PostCard post={topLiked} /> : <p>No post matches that.</p>}
                </div>
                <div className="analytics-box animate-fadeup">
                    <h2>Most Commented Post</h2>
                    {topCommented ? <PostCard post={topCommented} /> : <p>No post matches that.</p>}
                </div>
                <div className="analytics-box animate-fadeup">
                    <h2>Most Reblogged Post</h2>
                    {topReblogged ? <PostCard post={topReblogged} /> : <p>No post matches that.</p>}
                </div>
            </section>

            <form className="new-user-form animate-fadeup" onSubmit={handleCreate}>
                <h2>Create New User</h2>
                <div className="form-row">
                    <input
                        type="text"
                        placeholder="Search by username…"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        className="search-input"
                    />
                    <button type="button" onClick={() => loadAll()} className="btn-search">
                        🔍
                    </button>
                </div>
                <div className="form-row">
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
                    <button type="submit" className="btn-create">Create</button>
                </div>
            </form>

            {error && <div className="status error">{error}</div>}
            {loading ? (
                <div className="status">Loading…</div>
            ) : (
                <div className="table-container animate-fadein">
                    <table className="users-table animate-fadeup">
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
                                                <button onClick={() => handleSaveEdit(u.id)} className="btn-save">Save</button>
                                                <button onClick={handleCancelEdit} className="btn-cancel">Cancel</button>
                                            </>
                                        ) : (
                                            <>
                                                <button onClick={() => handleStartEdit(u)} className="btn-edit">Edit</button>
                                                <button onClick={() => handleDelete(u.id)} className="btn-delete">Delete</button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
