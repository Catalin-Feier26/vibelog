// src/components/DraftCard.jsx
import React, { useState } from 'react';
import { updatePost, deletePost } from '../api/postService';
import './DraftCard.css';

export default function DraftCard({ post, onDeleted, onUpdated }) {
    const [isEditing, setEditing] = useState(false);
    const [form, setForm] = useState({
        title: post.title,
        body: post.body
    });
    const [error, setError] = useState('');

    const save = async status => {
        setError('');
        try {
            await updatePost(post.id, { ...form, status });
            setEditing(false);
            onUpdated?.();
        } catch {
            setError('Failed to save');
        }
    };

    const handleDelete = async () => {
        await deletePost(post.id);
        onDeleted?.(post.id);
    };

    return (
        <div className="draft-card animate-fadein">
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

                    {/* --- render existing attachments in edit mode --- */}
                    {post.media && post.media.length > 0 && (
                        <div className="draft-media-container">
                            {post.media.map(m =>
                                m.type === 'IMG' ? (
                                    <img
                                        key={m.id}
                                        src={m.url}
                                        alt="draft attachment"
                                        className="draft-media-image"
                                    />
                                ) : (
                                    <video
                                        key={m.id}
                                        src={m.url}
                                        controls
                                        className="draft-media-video"
                                    />
                                )
                            )}
                        </div>
                    )}

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
                    <div className="draft-view">
                        <h2 className="draft-title">{post.title}</h2>
                        <p className="draft-body">{post.body}</p>

                        {/* --- render attachments in view mode --- */}
                        {post.media && post.media.length > 0 && (
                            <div className="draft-media-container">
                                {post.media.map(m =>
                                    m.type === 'IMG' ? (
                                        <img
                                            key={m.id}
                                            src={m.url}
                                            alt="draft attachment"
                                            className="draft-media-image"
                                        />
                                    ) : (
                                        <video
                                            key={m.id}
                                            src={m.url}
                                            controls
                                            className="draft-media-video"
                                        />
                                    )
                                )}
                            </div>
                        )}
                    </div>

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
