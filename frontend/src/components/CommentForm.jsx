import React, { useState }               from 'react';
import { addComment }                     from '../api/commentService';
import './CommentForm.css';

export default function CommentForm({ postId, onCommentAdded }) {
    const [content, setContent] = useState('');
    const [error, setError]     = useState('');

    const submit = async e => {
        e.preventDefault();
        setError('');
        try {
            await addComment(postId, { content });
            setContent('');
            onCommentAdded();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to post comment');
        }
    };

    return (
        <form onSubmit={submit} className="comment-form animate-fadeup">
            {error && <div className="comment-error">{error}</div>}
            <textarea
                value={content}
                onChange={e => setContent(e.target.value)}
                placeholder="Write a comment…"
                rows={3}
                required
            />
            <button type="submit" className="btn-comment">
                Comment
            </button>
        </form>
    );
}
