// src/pages/NewPostPage.jsx
import React, { useState } from 'react';
import { useNavigate }     from 'react-router-dom';
import {
    createPost,
    uploadPostMedia
} from '../api/postService';
import './NewPostPage.css';

export default function NewPostPage() {
    const [title, setTitle]       = useState('');
    const [body, setBody]         = useState('');
    const [files, setFiles]       = useState([]);        // selected files
    const [error, setError]       = useState('');
    const navigate                = useNavigate();

    const handleFileChange = e => {
        // convert FileList to Array
        setFiles(Array.from(e.target.files));
    };

    const submit = async status => {
        setError('');
        try {
            // 1) create the post
            const res = await createPost({ title, body, status });
            const postId = res.data.id;

            // 2) upload any selected files
            await Promise.all(
                files.map(file => {
                    const form = new FormData();
                    form.append('file', file);
                    return uploadPostMedia(postId, file);
                })
            );

            // 3) navigate back to feed (or to the new post’s page)
            navigate('/posts');
        } catch {
            setError('Could not save post');
        }
    };

    return (
        <div className="new-post-page animate-fadein">
            <form
                className="new-post-form animate-fadeup"
                onSubmit={e => e.preventDefault()}
            >
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

                {/* New: file input for media attachments */}
                <div className="form-group">
                    <label htmlFor="post-media">Attachments</label>
                    <input
                        id="post-media"
                        type="file"
                        accept="image/*,video/*"
                        multiple
                        onChange={handleFileChange}
                    />
                    {files.length > 0 && (
                        <ul className="selected-files">
                            {files.map((f, idx) => (
                                <li key={idx}>{f.name}</li>
                            ))}
                        </ul>
                    )}
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
