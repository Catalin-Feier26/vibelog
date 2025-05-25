// src/components/CommentList.jsx
import React, { useContext, useState, useEffect } from 'react';
import { AuthContext }           from '../contexts/AuthContext';
import { deleteComment, updateComment } from '../api/commentService';
import './CommentList.css';

export default function CommentList({
                                        comments: initialComments,
                                        onCommentDeleted,
                                        onCommentUpdated
                                    }) {
    const { user } = useContext(AuthContext);

    // local copy so we can mutate it for instant UI feedback
    const [comments, setComments]     = useState(initialComments);
    const [editingId, setEditingId]   = useState(null);
    const [editContent, setEditContent] = useState('');

    // sync local state when parent provides new data
    useEffect(() => {
        setComments(initialComments);
    }, [initialComments]);

    // delete handler: remove immediately and notify parent
    const handleDelete = async id => {
        try {
            await deleteComment(id);
            setComments(cs => cs.filter(c => c.id !== id));
            onCommentDeleted && onCommentDeleted();
        } catch (err) {
            console.error('Failed to delete comment', err);
        }
    };

    // start inline edit
    const handleStartEdit = comment => {
        setEditingId(comment.id);
        setEditContent(comment.content);
    };

    // save inline edit: update backend & UI
    const handleSaveEdit = async id => {
        try {
            const res = await updateComment(id, { content: editContent });
            // update this comment locally
            setComments(cs =>
                cs.map(c => (c.id === id ? { ...c, content: res.data.content, createdAt: res.data.createdAt } : c))
            );
            setEditingId(null);
            onCommentUpdated && onCommentUpdated();
        } catch (err) {
            console.error('Failed to save comment edit', err);
        }
    };

    // cancel edit mode
    const handleCancelEdit = () => {
        setEditingId(null);
    };

    return (
        <ul className="comment-list">
            {comments.length === 0 && (
                <li className="no-comments">No comments yet.</li>
            )}

            {comments.map(c => {
                const isAuthor = user?.username === c.authorUsername;
                const isEditing = editingId === c.id;

                return (
                    <li key={c.id} className="comment-item">
                        <div className="comment-header">
                            <strong>@{c.authorUsername}</strong>
                            <span className="comment-timestamp">
                {new Date(c.createdAt).toLocaleTimeString()}
              </span>

                            {isAuthor && !isEditing && (
                                <>
                                    <button
                                        className="btn-edit-comment"
                                        onClick={() => handleStartEdit(c)}
                                    >
                                        ✏️
                                    </button>
                                    <button
                                        className="btn-delete-comment"
                                        onClick={() => handleDelete(c.id)}
                                    >
                                        🗑️
                                    </button>
                                </>
                            )}
                        </div>

                        {isEditing ? (
                            <div className="comment-edit-form">
                <textarea
                    rows={2}
                    value={editContent}
                    onChange={e => setEditContent(e.target.value)}
                />
                                <button
                                    className="btn-save-comment"
                                    onClick={() => handleSaveEdit(c.id)}
                                >
                                    Save
                                </button>
                                <button
                                    className="btn-cancel-comment"
                                    onClick={handleCancelEdit}
                                >
                                    Cancel
                                </button>
                            </div>
                        ) : (
                            <p className="comment-content">{c.content}</p>
                        )}
                    </li>
                );
            })}
        </ul>
    );
}
