// src/pages/ModerationPage.jsx
import React, { useEffect, useState } from 'react';
import {
    getReportsByStatus,
    updateReportStatus
} from '../api/reportService';
import {
    deletePostAsMod,
    deleteCommentAsMod
} from '../api/moderationService';
import './ModerationPage.css';

export default function ModerationPage() {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState('');

    // fetch the pending reports
    const load = async () => {
        setError('');
        setLoading(true);
        try {
            const res = await getReportsByStatus('PENDING', 0, 50);
            setReports(res.data.content);
        } catch (e) {
            console.error(e);
            setError('Failed to load reports');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    // change status to REVIEWED or RESOLVED
    const handleStatus = async (id, status) => {
        try {
            await updateReportStatus(id, status);
            // reload the list so we never show stale data
            load();
        } catch (e) {
            console.error(e);
            alert('Could not update report status');
        }
    };

    // delete the offending content (post or comment), then reload
    const handleDeleteContent = async r => {
        try {
            if (r.postId) {
                await deletePostAsMod(r.postId);
            } else {
                await deleteCommentAsMod(r.commentId);
            }

            // after removing the content we *could* try to mark the report resolved,
            // but often the report row itself has already been deleted on the backend
            // so we quietly ignore a 404 from updateReportStatus
            try {
                await updateReportStatus(r.id, 'RESOLVED');
            } catch (inner) {
                if (!(inner.response && inner.response.status === 404)) {
                    console.error('Failed to resolve report', inner);
                }
            }

            // finally, reload the fresh list
            load();
        } catch (e) {
            console.error(e);
            alert('Failed to delete content');
        }
    };

    if (loading) return <div className="mod-page">Loading…</div>;
    if (error)   return <div className="mod-page error">{error}</div>;
    if (reports.length === 0) {
        return <div className="mod-page empty">No pending reports.</div>;
    }

    return (
        <div className="mod-page">
            <h1>Moderation — Pending Reports</h1>
            <table className="mod-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Reporter</th>
                    <th>Target</th>
                    <th>Reason</th>
                    <th>When</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {reports.map(r => (
                    <tr key={r.id}>
                        <td>{r.id}</td>
                        <td>@{r.reporterUsername}</td>
                        <td>
                            {r.postId
                                ? <a href={`/posts/${r.postId}`}>Post #{r.postId}</a>
                                : <span>Comment #{r.commentId}</span>
                            }
                        </td>
                        <td className="reason">{r.reason}</td>
                        <td>{new Date(r.reportedAt).toLocaleString()}</td>
                        <td className="actions">
                            <button
                                onClick={() => handleStatus(r.id, 'REVIEWED')}
                                className="btn-review"
                            >
                                Review
                            </button>
                            <button
                                onClick={() => handleStatus(r.id, 'RESOLVED')}
                                className="btn-resolve"
                            >
                                Resolve
                            </button>
                            <button
                                onClick={() => handleDeleteContent(r)}
                                className="btn-delete"
                            >
                                Delete Content
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
