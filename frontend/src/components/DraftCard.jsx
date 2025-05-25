import React, { useState } from 'react';
import { updatePost, deletePost } from '../api/postService';
import './DraftCard.css';

export default function DraftCard({ post, onDeleted, onUpdated }) {
    const [isEditing, setEditing] = useState(false);
    const [form, setForm]         = useState({
        title: post.title,
        body:  post.body
    });
    const [error, setError]       = useState('');

    // Delete handler
    const handleDelete = async () => {
        await deletePost(post.id);
        onDeleted && onDeleted(post.id);
    };

    // Generic save function
    const save = async status => {
        setError('');
        try {
            await updatePost(post.id, {
                title:  form.title,
                body:   form.body,
                status
            });
            setEditing(false);
            onUpdated && onUpdated();
        } catch (e) {
            setError('Failed to save');
        }
    };

    return (
        <div className="draft-card">
            {isEditing ? (
                <form
                    className="draft-edit-form"
                    onSubmit={e => {
                        e.preventDefault();
                        save('DRAFT');
                    }}
                >
                    {error && <div className="draft-error">{error}</div>}

                    <input
                        type="text"
                        className="draft-input-title"
                        value={form.title}
                        onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
                        placeholder="Title"
                        maxLength={200}
                        required
                    />

                    <textarea
                        className="draft-input-body"
                        value={form.body}
                        onChange={e => setForm(f => ({ ...f, body: e.target.value }))}
                        placeholder="Body"
                        rows={4}
                        required
                    />

                    <div className="draft-actions">
                        <button
                            type="button"
                            className="btn-draft-save"
                            onClick={() => save('DRAFT')}
                        >
                            Save
                        </button>
                        <button
                            type="button"
                            className="btn-draft-publish"
                            onClick={() => save('PUBLISHED')}
                        >
                            Publish
                        </button>
                        <button
                            type="button"
                            className="btn-draft-cancel"
                            onClick={() => setEditing(false)}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            ) : (
                <>
                    <h2 className="draft-title">{post.title}</h2>
                    <p className="draft-body">{post.body}</p>

                    <div className="draft-actions">
                        <button
                            className="btn-draft-edit"
                            onClick={() => setEditing(true)}
                        >
                            Edit
                        </button>
                        <button
                            className="btn-draft-publish"
                            onClick={() => save('PUBLISHED')}
                        >
                            Publish
                        </button>
                        <button
                            className="btn-draft-delete"
                            onClick={handleDelete}
                        >
                            Delete
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
