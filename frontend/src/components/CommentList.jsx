import React, { useContext, useState, useEffect } from 'react';
import { AuthContext }                           from '../contexts/AuthContext';
import { deleteComment, updateComment }          from '../api/commentService';
import ReportForm                                from './ReportForm';
import './CommentList.css';

export default function CommentList({
                                        comments: initialComments,
                                        onCommentDeleted,
                                        onCommentUpdated
                                    }) {
    const { user } = useContext(AuthContext);
    const [comments, setComments]       = useState(initialComments);
    const [editingId, setEditingId]     = useState(null);
    const [editContent, setEditContent] = useState('');
    const [reportingId, setReportingId] = useState(null);

    useEffect(() => {
        setComments(initialComments);
    }, [initialComments]);

    const handleDelete = async id => {
        try {
            await deleteComment(id);
            setComments(cs => cs.filter(c => c.id !== id));
            onCommentDeleted?.();
        } catch {
            console.error('Failed to delete comment');
        }
    };

    const handleStartEdit = c => {
        setEditingId(c.id);
        setEditContent(c.content);
    };

    const handleSaveEdit = async id => {
        try {
            const res = await updateComment(id, { content: editContent });
            setComments(cs =>
                cs.map(c =>
                    c.id === id
                        ? { ...c, content: res.data.content, createdAt: res.data.createdAt }
                        : c
                )
            );
            setEditingId(null);
            onCommentUpdated?.();
        } catch {
            console.error('Failed to save comment edit');
        }
    };

    return (
        <ul className="comment-list">
            {comments.length === 0 && (
                <li className="no-comments">No comments yet.</li>
            )}

            {comments.map(c => {
                const isAuthor   = user?.username === c.authorUsername;
                const isEditing  = editingId === c.id;
                const isReporting= reportingId === c.id;

                return (
                    <li key={c.id} className="comment-item animate-fadein">
                        <div className="comment-header">
                            <strong className="comment-author">
                                @{c.authorUsername}
                            </strong>
                            <span className="comment-timestamp">
                {new Date(c.createdAt).toLocaleTimeString()}
              </span>
                            {!isEditing && (
                                isAuthor
                                    ? <>
                                        <button
                                            className="btn-edit-comment"
                                            onClick={() => handleStartEdit(c)}
                                        >✏️</button>
                                        <button
                                            className="btn-delete-comment"
                                            onClick={() => handleDelete(c.id)}
                                        >🗑️</button>
                                    </>
                                    : <button
                                        className="btn-report-comment"
                                        onClick={() =>
                                            setReportingId(isReporting ? null : c.id)
                                        }
                                    >🚩</button>
                            )}
                        </div>

                        {isEditing ? (
                            <div className="comment-edit-form">
                <textarea
                    rows={2}
                    value={editContent}
                    onChange={e => setEditContent(e.target.value)}
                    className="edit-textarea"
                />
                                <button
                                    className="btn-save-comment"
                                    onClick={() => handleSaveEdit(c.id)}
                                >Save</button>
                                <button
                                    className="btn-cancel-comment"
                                    onClick={() => setEditingId(null)}
                                >Cancel</button>
                            </div>
                        ) : (
                            <p className="comment-content">{c.content}</p>
                        )}

                        {isReporting && (
                            <ReportForm
                                type="comment"
                                targetId={c.id}
                                onCancel={() => setReportingId(null)}
                                onReported={() => setReportingId(null)}
                            />
                        )}
                    </li>
                );
            })}
        </ul>
    );
}
