import React, { useState } from 'react';
import { useNavigate }     from 'react-router-dom';
import { createPost }      from '../api/postService';
import './NewPostPage.css';

export default function NewPostPage() {
    const [title, setTitle]   = useState('');
    const [body, setBody]     = useState('');
    const [error, setError]   = useState('');
    const navigate            = useNavigate();

    const submit = async status => {
        setError('');
        try {
            await createPost({ title, body, status });
            navigate('/posts');
        } catch {
            setError('Could not save post');
        }
    };

    return (
        <div className="new-post-page animate-fadein">
            <form className="new-post-form animate-fadeup" onSubmit={e => e.preventDefault()}>
                <h2>Create New Post</h2>
                {error && <div className="form-error">{error}</div>}

                <div className="form-group">
                    <label htmlFor="post-title">Title</label>
                    <input
                        id="post-title"
                        type="text"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        placeholder="Give your post a headline"
                        maxLength={100}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="post-body">Body</label>
                    <textarea
                        id="post-body"
                        value={body}
                        onChange={e => setBody(e.target.value)}
                        placeholder="What's on your mind?"
                        rows={6}
                        required
                    />
                </div>

                <div className="form-actions">
                    <button
                        type="button"
                        className="btn-draft"
                        onClick={() => submit('DRAFT')}
                    >
                        Save as Draft
                    </button>
                    <button
                        type="button"
                        className="btn-submit"
                        onClick={() => submit('PUBLISHED')}
                    >
                        Publish
                    </button>
                </div>
            </form>
        </div>
    );
}
