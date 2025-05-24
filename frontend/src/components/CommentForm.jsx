import React, { useState } from 'react';
import { addComment } from '../api/commentService';
import './CommentForm.css';

export default function CommentForm({ postId, onCommentAdded }) {
    const [content, setContent] = useState('');
    const [error, setError]     = useState('');

    const submit = async e => {
        e.preventDefault();
        setError('');
        try {
            // Send the field your DTO expects
            await addComment(postId, { content });
            setContent('');
            onCommentAdded();
        } catch (err) {
            // Surface the validation message if present
            setError(err.response?.data?.message || 'Failed to post comment');
        }
    };

    return (
        <form onSubmit={submit} className="comment-form">
            {error && <div className="comment-error">{error}</div>}
            <textarea
                value={content}
                onChange={e => setContent(e.target.value)}
                placeholder="Write a comment…"
                rows={3}
                required
            />
            <button type="submit">Comment</button>
        </form>
    );
}
