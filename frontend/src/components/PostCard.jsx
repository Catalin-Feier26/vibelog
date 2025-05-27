import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { toggleLike, getLikes } from '../api/likeService';
import { getCommentsForPost } from '../api/commentService';
import {
    getReblogState,
    getReblogCount,
    reblogPost,
    undoReblogPost
} from '../api/postService';
import CommentList from './CommentList';
import CommentForm from './CommentForm';
import ReportForm from './ReportForm';
import './PostCard.css';

export default function PostCard({ post, onDeleted, onReblog }) {
    const [likesCount, setLikesCount]     = useState(post.likeCount || 0);
    const [likedByMe, setLikedByMe]       = useState(false);
    const [commentCount, setCommentCount] = useState(0);
    const [comments, setComments]         = useState([]);
    const [showComments, setShowComments] = useState(false);

    const [reblogged, setReblogged]     = useState(false);
    const [reblogCount, setReblogCount] = useState(post.reblogCount || 0);

    const [reporting, setReporting] = useState(false);

    useEffect(() => {
        getLikes(post.id)
            .then(res => {
                setLikesCount(res.data.totalLikes);
                setLikedByMe(res.data.liked);
            })
            .catch(console.error);

        getCommentsForPost(post.id)
            .then(res => setCommentCount(res.data.length))
            .catch(console.error);

        getReblogState(post.id)
            .then(setReblogged)
            .catch(console.error);

        getReblogCount(post.id)
            .then(setReblogCount)
            .catch(console.error);
    }, [post.id]);

    const handleLike = async () => {
        try {
            const res = await toggleLike(post.id);
            setLikesCount(res.data.totalLikes);
            setLikedByMe(res.data.liked);
        } catch (e) {
            console.error(e);
        }
    };

    const loadComments = async () => {
        try {
            const res = await getCommentsForPost(post.id);
            setComments(res.data);
            setCommentCount(res.data.length);
        } catch (e) {
            console.error(e);
        }
    };

    const handleToggleComments = () => {
        if (!showComments) loadComments();
        setShowComments(v => !v);
    };

    const handleDelete = () => onDeleted?.(post.id);

    const handleReblog = async () => {
        try {
            if (reblogged) {
                await undoReblogPost(post.id);
                setReblogCount(c => c - 1);
            } else {
                await reblogPost(post.id);
                setReblogCount(c => c + 1);
            }
            setReblogged(r => !r);
            onReblog?.();
        } catch (e) {
            console.error(e);
        }
    };

    return (
        <div className={`post-card animate-fadein ${reblogged ? 'is-reblog' : ''}`}>
            {post.originalPostId && (
                <div className="reblog-banner">
                    🔁 <strong>@{post.authorUsername}</strong> reblogged{' '}
                    <Link
                        to={`/posts/${post.originalPostId}`}
                        className="reblog-link"
                    >
                        @{post.originalAuthorUsername}
                    </Link>
                </div>
            )}

            <header className="post-header">
                <Link to={`/users/${post.authorUsername}`} className="author-link">
                    <strong>@{post.authorUsername}</strong>
                </Link>
                <Link to={`/posts/${post.id}`} className="time-link">
                    <span>{new Date(post.createdAt).toLocaleString()}</span>
                </Link>
            </header>

            <Link to={`/posts/${post.id}`} className="post-link">
                <p className="post-body">{post.body}</p>
            </Link>

            <footer className="post-footer">
                <button
                    onClick={handleLike}
                    className={`btn-like ${likedByMe ? 'liked' : ''}`}
                >
                    {likedByMe ? '💔' : '❤️'} {likesCount}
                </button>

                <button onClick={handleToggleComments} className="btn-comment">
                    💬 {commentCount}
                </button>

                <button
                    onClick={handleReblog}
                    className={`btn-reblog ${reblogged ? 'reblogged' : ''}`}
                >
                    {reblogged ? '🔁' : '↪️'} {reblogCount}
                </button>

                <button onClick={handleDelete} className="btn-delete">
                    🗑️
                </button>

                <button
                    onClick={() => setReporting(r => !r)}
                    className="btn-report"
                >
                    🚩 Report
                </button>
            </footer>

            {reporting && (
                <ReportForm
                    type="post"
                    targetId={post.id}
                    onCancel={() => setReporting(false)}
                    onReported={() => setReporting(false)}
                />
            )}

            {showComments && (
                <div className="comment-panel">
                    <CommentList
                        comments={comments}
                        onCommentDeleted={loadComments}
                        onCommentUpdated={loadComments}
                    />
                    <CommentForm postId={post.id} onCommentAdded={loadComments} />
                </div>
            )}
        </div>
    );
}