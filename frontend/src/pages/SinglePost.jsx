// src/pages/SinglePost.jsx
import React, { useEffect, useState, useContext } from 'react';
import { useParams, Link }     from 'react-router-dom';
import { AuthContext }         from '../contexts/AuthContext';
import {
    getPostById,
    updatePost,
    deletePost
} from '../api/postService';
import { getCommentsForPost }  from '../api/commentService';
import CommentList             from '../components/CommentList';
import CommentForm             from '../components/CommentForm';
import './SinglePost.css';

export default function SinglePost() {
    const { postId } = useParams();
    const { user }   = useContext(AuthContext);

    const [post, setPost]         = useState(null);
    const [comments, setComments] = useState([]);
    const [isEditing, setEditing] = useState(false);
    const [form, setForm]         = useState({ title: '', body: '' });
    const [error, setError]       = useState('');

    // ① initial load of both post & comments, only depends on postId
    useEffect(() => {
        const fetchAll = async () => {
            try {
                // fetch post
                const pRes = await getPostById(postId);
                setPost(pRes.data);
                setForm({ title: pRes.data.title, body: pRes.data.body });

                // fetch comments
                const cRes = await getCommentsForPost(postId);
                setComments(cRes.data);
            } catch (e) {
                console.error('Error loading post or comments', e);
            }
        };
        fetchAll();
    }, [postId]);

    // helper for comment refresh
    const loadComments = async () => {
        try {
            const cRes = await getCommentsForPost(postId);
            setComments(cRes.data);
        } catch (e) {
            console.error('Failed to load comments', e);
        }
    };

    // ② save the edited post using the returned DTO
    const handleSave = async e => {
        e.preventDefault();
        setError('');
        try {
            const updated = await updatePost(postId, {
                title:  form.title,
                body:   form.body,
                status: post.status
            });
            setEditing(false);
            setPost(updated);
            setForm({ title: updated.title, body: updated.body });
        } catch {
            setError('Could not save changes');
        }
    };

    const handleDelete = async () => {
        await deletePost(postId);
        window.location.href = '/posts';
    };

    if (!post) return <div>Loading…</div>;

    const isAuthor = user?.username === post.authorUsername;

    return (
        <div className="single-post-page">
            <div className="single-nav">
                <Link to="/posts">← Back to Feed</Link>
                {isAuthor && (
                    <>
                        <button
                            onClick={() => setEditing(v => !v)}
                            className="btn-edit"
                        >
                            {isEditing ? 'Cancel' : 'Edit'}
                        </button>
                        {!isEditing && (
                            <button onClick={handleDelete} className="btn-delete">
                                Delete
                            </button>
                        )}
                    </>
                )}
            </div>

            {isEditing ? (
                <form className="edit-post-form" onSubmit={handleSave}>
                    {error && <div className="form-error">{error}</div>}
                    <div className="form-group">
                        <label>Title</label>
                        <input
                            value={form.title}
                            onChange={e =>
                                setForm(f => ({ ...f, title: e.target.value }))
                            }
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Body</label>
                        <textarea
                            rows={6}
                            value={form.body}
                            onChange={e =>
                                setForm(f => ({ ...f, body: e.target.value }))
                            }
                            required
                        />
                    </div>
                    <button type="submit" className="btn-save">
                        Save
                    </button>
                </form>
            ) : (
                <>
                    <h1 className="post-title">{post.title}</h1>
                    <div className="post-meta">
                        <span>by @{post.authorUsername}</span>
                        <span>{new Date(post.createdAt).toLocaleString()}</span>
                    </div>
                    <p className="post-body">{post.body}</p>
                </>
            )}

            <div className="comments-section">
                <h3>Comments</h3>
                <CommentList
                    comments={comments}
                    onCommentDeleted={loadComments}
                    onCommentUpdated={loadComments}
                />
                <CommentForm postId={postId} onCommentAdded={loadComments} />
            </div>
        </div>
    );
}
