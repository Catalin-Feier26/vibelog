// SinglePost.jsx
import React, { useEffect, useState, useContext } from 'react';
import { useParams, Link } from 'react-router-dom';
import { AuthContext }     from '../contexts/AuthContext';
import { getPostById, updatePost, deletePost } from '../api/postService';
import { getCommentsForPost } from '../api/commentService';
import CommentList  from '../components/CommentList';
import CommentForm  from '../components/CommentForm';
import './SinglePost.css';

export default function SinglePost() {
    const { postId } = useParams();
    const { user }   = useContext(AuthContext);

    const [post, setPost]         = useState(null);
    const [comments, setComments] = useState([]);
    const [isEditing, setEditing] = useState(false);
    const [form, setForm]         = useState({ title: '', body: '' });
    const [error, setError]       = useState('');

    // Load post & comments
    useEffect(() => {
        (async () => {
            try {
                const pRes = await getPostById(postId);
                setPost(pRes.data);
                setForm({ title: pRes.data.title, body: pRes.data.body });
                const cRes = await getCommentsForPost(postId);
                setComments(cRes.data);
            } catch {
                console.error('Error loading post or comments');
            }
        })();
    }, [postId]);

    const loadComments = async () => {
        try {
            const cRes = await getCommentsForPost(postId);
            setComments(cRes.data);
        } catch {
            console.error('Failed to load comments');
        }
    };

    const handleSave = async e => {
        e.preventDefault();
        setError('');
        try {
            const updated = await updatePost(postId, { ...form, status: post.status });
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

    if (!post) return <div className="single-loading">Loading…</div>;

    const isAuthor = user?.username === post.authorUsername;

    return (
        <div className="single-post-page animate-fadein">
            <div className="single-nav">
                <Link to="/posts" className="btn-back">← Back to Feed</Link>
                {isAuthor && (
                    <div>
                        <button
                            className="btn-edit"
                            onClick={() => setEditing(v => !v)}
                        >
                            {isEditing ? 'Cancel' : 'Edit'}
                        </button>
                        {!isEditing && (
                            <button
                                className="btn-delete"
                                onClick={handleDelete}
                            >
                                Delete
                            </button>
                        )}
                    </div>
                )}
            </div>

            {isEditing ? (
                <form
                    className="edit-post-form animate-fadeup"
                    onSubmit={handleSave}
                >
                    {error && <div className="form-error">{error}</div>}
                    <div className="form-group">
                        <label>Title</label>
                        <input
                            className="edit-input"
                            value={form.title}
                            onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Body</label>
                        <textarea
                            className="edit-textarea"
                            rows={6}
                            value={form.body}
                            onChange={e => setForm(f => ({ ...f, body: e.target.value }))}
                            required
                        />
                    </div>
                    <button type="submit" className="btn-save">Save</button>
                </form>
            ) : (
                <div className="post-view">
                    <h1 className="post-title">{post.title}</h1>
                    <div className="post-meta">
                        <span className="post-author">@{post.authorUsername}</span>
                        <span className="post-date">
              {new Date(post.createdAt).toLocaleString()}
            </span>
                    </div>
                    <p className="post-body">{post.body}</p>
                </div>
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
