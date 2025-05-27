import React, { useEffect, useState } from 'react';
import { getMyDrafts }    from '../api/postService';
import DraftCard          from '../components/DraftCard';
import './DraftsPage.css';

export default function DraftsPage() {
    const [drafts, setDrafts]   = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState('');

    const loadDrafts = async () => {
        setError(''); setLoading(true);
        try {
            const res = await getMyDrafts();
            setDrafts(res.data.content);
        } catch {
            setError('Failed to load drafts');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadDrafts();
    }, []);

    const handleDeleted = id => {
        setDrafts(ds => ds.filter(d => d.id !== id));
    };

    return (
        <div className="drafts-page animate-fadein">
            <h1 className="page-title">My Drafts</h1>

            {error && <div className="status error">{error}</div>}
            {loading ? (
                <div className="status loading">Loading…</div>
            ) : drafts.length === 0 ? (
                <div className="status empty">No drafts yet.</div>
            ) : (
                <div className="drafts-grid">
                    {drafts.map(d => (
                        <DraftCard
                            key={d.id}
                            post={d}
                            onDeleted={handleDeleted}
                            onUpdated={loadDrafts}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}
