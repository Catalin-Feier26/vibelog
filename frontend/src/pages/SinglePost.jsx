import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getPostById, deletePost } from '../api/postService';
import { getCommentsForPost } from '../api/commentService';
import PostCard from '../components/PostCard';
import CommentList from '../components/CommentList';
import CommentForm from '../components/CommentForm';
import './SinglePost.css';

export default function SinglePost() {
    const { postId } = useParams();
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        (async () => {
            try {
                const p = await getPostById(postId);
                setPost(p.data);
                const c = await getCommentsForPost(postId);
                setComments(c.data);
            } catch {
                setError('Failed to load post');
            }
        })();
    }, [postId]);

    const reloadComments = async () => {
        const c = await getCommentsForPost(postId);
        setComments(c.data);
    };

    const handleDelete = async () => {
        await deletePost(postId);
        // after deletion, send back to feed
        window.location.href = '/posts';
    };

    if (error) return <div className="single-error">{error}</div>;
    if (!post) return <div className="single-loading">Loading…</div>;

    return (
        <div className="single-post-page">
            <div className="single-nav">
                <Link to="/posts">← Back to Feed</Link>
                <button onClick={handleDelete} className="btn-delete">Delete Post</button>
            </div>
            <PostCard post={post} />
            <div className="comments-section">
                <h3>Comments</h3>
                <CommentList comments={comments} />
                <CommentForm postId={postId} onCommentAdded={reloadComments} />
            </div>
        </div>
    );
}
