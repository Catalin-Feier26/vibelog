// ModerationPage.jsx
import React, { useEffect, useState } from 'react';
import { Link }                          from 'react-router-dom';
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

    const load = async () => {
        setError('');
        setLoading(true);
        try {
            const res = await getReportsByStatus('PENDING', 0, 50);
            setReports(res.data.content);
        } catch {
            setError('Failed to load reports');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const handleStatus = async (id, status) => {
        try {
            await updateReportStatus(id, status);
            load();
        } catch {
            alert('Could not update report status');
        }
    };

    const handleDeleteContent = async r => {
        try {
            if (r.postId) {
                await deletePostAsMod(r.postId);
            } else {
                await deleteCommentAsMod(r.commentId);
            }
            // resolve report if needed
            try {
                await updateReportStatus(r.id, 'RESOLVED');
            } catch {}
            load();
        } catch {
            alert('Failed to delete content');
        }
    };

    if (loading) {
        return <div className="mod-page animate-fadein">Loading…</div>;
    }
    if (error) {
        return <div className="mod-page animate-fadein error">{error}</div>;
    }
    if (reports.length === 0) {
        return (
            <div className="mod-page animate-fadein empty">
                No pending reports.
            </div>
        );
    }

    return (
        <div className="mod-page animate-fadein">
            <h1 className="page-title animate-fadeup">
                Moderation — Pending Reports
            </h1>

            <div className="table-wrapper animate-fadeup">
                <table className="mod-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Reporter</th>
                        <th>Target</th>
                        <th className="reason-col">Reason</th>
                        <th>When</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {reports.map(r => (
                        <tr key={r.id}>
                            <td>{r.id}</td>
                            <td>
                                <Link to={`/users/${r.reporterUsername}`}>
                                    @{r.reporterUsername}
                                </Link>
                            </td>
                            <td>
                                {r.postId ? (
                                    <Link to={`/posts/${r.postId}`}>Post #{r.postId}</Link>
                                ) : (
                                    <span>Comment #{r.commentId}</span>
                                )}
                            </td>
                            <td className="reason-col">{r.reason}</td>
                            <td>
                                {new Date(r.reportedAt).toLocaleString()}
                            </td>
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
        </div>
    );
}
