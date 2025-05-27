import React, { useEffect, useState } from 'react';
import { Link }                      from 'react-router-dom';
import { getAllPosts, deletePost }   from '../api/postService';
import PostCard                      from '../components/PostCard';
import './PostFeed.css';

export default function PostsFeed() {
    const [posts, setPosts]     = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState('');

    useEffect(() => {
        load();
    }, []);

    const load = async () => {
        setError('');
        setLoading(true);
        try {
            const res = await getAllPosts();
            setPosts(res.data.content);
        } catch {
            setError('Failed to load posts');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleted = async id => {
        try {
            await deletePost(id);
            load();
        } catch {
            // optionally toast error
        }
    };

    const handleReblog = () => {
        load();
    };

    return (
        <div className="posts-feed animate-fadein">
            <div className="feed-header">
                <h1>Feed</h1>
                <Link to="/posts/new" className="btn-new-post">
                    + New Post
                </Link>
            </div>

            {error && <div className="feed-status error">{error}</div>}
            {loading && <div className="feed-status loading">Loading posts…</div>}
            {!loading && posts.length === 0 && (
                <div className="feed-status empty">No posts yet.</div>
            )}

            {!loading && posts.map(p => (
                <PostCard
                    key={p.id}
                    post={p}
                    onDeleted={handleDeleted}
                    onReblog={handleReblog}
                />
            ))}
        </div>
    );
}
