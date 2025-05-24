import React from 'react';
import { deleteComment } from '../api/commentService';
import './CommentList.css';

export default function CommentList({ comments, onCommentDeleted }) {
    if (!comments.length) return <p className="no-comments">No comments yet.</p>;

    const handleDelete = async id => {
        try {
            await deleteComment(id);
            onCommentDeleted();
        } catch (err) {
            console.error('Failed to delete comment', err);
        }
    };

    return (
        <ul className="comment-list">
            {comments.map(c => (
                <li key={c.id} className="comment-item">
                    <div className="comment-header">
                        <strong>@{c.authorUsername}</strong>
                        <button
                            className="btn-delete-comment"
                            onClick={() => handleDelete(c.id)}
                        >
                            🗑️
                        </button>
                    </div>
                    <p className="comment-content">{c.content}</p>
                    <small className="comment-timestamp">
                        {new Date(c.createdAt).toLocaleTimeString()}
                    </small>
                </li>
            ))}
        </ul>
    );
}