import React, { useState, useEffect } from 'react';
import { toggleLike, getLikes }           from '../api/likeService';
import { getCommentsForPost }             from '../api/commentService';
import CommentList                        from './CommentList';
import CommentForm                        from './CommentForm';
import './PostCard.css';

export default function PostCard({ post, onDeleted }) {
    const [likesCount, setLikesCount]     = useState(post.likesCount || 0);
    const [likedByMe, setLikedByMe]       = useState(false);
    const [showComments, setShowComments] = useState(false);
    const [comments, setComments]         = useState([]);
    const [commentCount, setCommentCount] = useState(0);

    // 1️⃣ Fetch initial like status & count
    useEffect(() => {
        (async () => {
            try {
                const res = await getLikes(post.id);
                setLikesCount(res.data.totalLikes);
                setLikedByMe(res.data.liked);
            } catch (e) {
                console.error('Failed to fetch likes', e);
            }
        })();
    }, [post.id]);

    // 2️⃣ Fetch comment count only (not full list) on mount
    useEffect(() => {
        (async () => {
            try {
                const res = await getCommentsForPost(post.id);
                setCommentCount(res.data.length);
            } catch (e) {
                console.error('Failed to fetch comment count', e);
            }
        })();
    }, [post.id]);

    // helper to load full comments & sync count
    const loadComments = async () => {
        try {
            const res = await getCommentsForPost(post.id);
            setComments(res.data);
            setCommentCount(res.data.length);
        } catch (e) {
            console.error('Failed to load comments', e);
        }
    };

    // toggle the comments panel
    const handleToggleComments = () => {
        if (!showComments) loadComments();
        setShowComments(v => !v);
    };

    const handleLike = async () => {
        try {
            const res = await toggleLike(post.id);
            setLikesCount(res.data.totalLikes);
            setLikedByMe(res.data.liked);
        } catch (e) {
            console.error('Failed to toggle like', e);
        }
    };

    const handleDelete = () => onDeleted && onDeleted(post.id);

    return (
        <div className="post-card">
            <header>
                <strong>@{post.authorUsername}</strong>
                <span>{new Date(post.createdAt).toLocaleString()}</span>
            </header>

            <p>{post.body}</p>

            <footer>
                <button
                    onClick={handleLike}
                    className={likedByMe ? 'btn-like liked' : 'btn-like'}
                >
                    {likedByMe ? '💔' : '❤️'} {likesCount}
                </button>

                <button onClick={handleToggleComments} className="btn-comment">
                    💬 {commentCount}
                </button>

                <button onClick={handleDelete} className="btn-delete">
                    🗑️
                </button>
            </footer>

            {showComments && (
                <div className="comment-panel">
                    <CommentList
                        comments={comments}
                        onCommentDeleted={loadComments}
                    />
                    <CommentForm
                        postId={post.id}
                        onCommentAdded={loadComments}
                    />
                </div>
            )}
        </div>
    );
}
