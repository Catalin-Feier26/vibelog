import React, { useEffect, useState } from 'react';
import {
    getUsers,
    createUser,
    updateUser,
    deleteUser
} from '../api/adminService';
import './AdminUsersPage.css';

export default function AdminUsersPage() {
    const [users, setUsers]           = useState([]);
    const [loading, setLoading]       = useState(true);
    const [error, setError]           = useState('');
    const [page, setPage]             = useState(0);
    const [size]                      = useState(50);
    const [search, setSearch]         = useState('');

    // new-user form
    const [newUser, setNewUser]       = useState({ email: '', username: '', password: '' });

    // inline edit state
    const [editingId, setEditingId]   = useState(null);
    const [editData, setEditData]     = useState({ email: '', username: '' });

    // load list (with optional search)
    const load = async () => {
        setError('');
        setLoading(true);
        try {
            const res = await getUsers(search, page, size);
            setUsers(res.data.content);
        } catch (e) {
            console.error(e);
            setError('Could not fetch users');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(); }, [page, search]);

    // create handler
    const handleCreate = async e => {
        e.preventDefault();
        try {
            await createUser(newUser);
            setNewUser({ email: '', username: '', password: '' });
            load();
        } catch (e) {
            console.error(e);
            alert('Failed to create user');
        }
    };

    // start editing
    const handleStartEdit = user => {
        setEditingId(user.id);
        setEditData({ email: user.email, username: user.username });
    };

    // save edit
    const handleSaveEdit = async id => {
        try {
            await updateUser(id, editData);
            setEditingId(null);
            load();
        } catch (e) {
            console.error(e);
            alert('Failed to update user');
        }
    };

    // cancel edit
    const handleCancelEdit = () => {
        setEditingId(null);
    };

    // delete user
    const handleDelete = async id => {
        if (!window.confirm(`Really delete user #${id}?`)) return;
        try {
            await deleteUser(id);
            load();
        } catch (e) {
            console.error(e);
            alert('Failed to delete user');
        }
    };

    return (
        <div className="admin-users-page">
            <h1>Admin: User Management</h1>

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
                <div>Loading users…</div>
            ) : (
                <table className="users-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Email</th>
                        <th>Username</th>
                        <th>Roles</th>
                        <th>Actions</th>
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
