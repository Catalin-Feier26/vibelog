// MyReports.jsx
import React, { useEffect, useState } from 'react';
import { getMyReports }              from '../api/reportService';
import './MyReports.css';

export default function MyReports() {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState('');

    useEffect(() => {
        load();
    }, []);

    const load = async () => {
        setError('');
        setLoading(true);
        try {
            const res = await getMyReports();
            setReports(res.data.content);
        } catch {
            setError('Failed to load your reports');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="my-reports-page animate-fadein">
            <h1 className="page-title">My Reports</h1>

            {loading && <div className="status">Loading…</div>}
            {error   && <div className="status error">{error}</div>}
            {!loading && !error && reports.length === 0 && (
                <div className="status empty">
                    You haven’t filed any reports yet.
                </div>
            )}

            <ul className="report-list">
                {reports.map(r => (
                    <li key={r.id} className="report-item animate-fadeup">
                        <div className="report-meta">
                            <span>ID: {r.id}</span> ·{' '}
                            <span>
                {r.postId
                    ? `Post #${r.postId}`
                    : `Comment #${r.commentId}`}
              </span> ·{' '}
                            <span>Status: {r.status}</span> ·{' '}
                            <span>
                Reported: {new Date(r.reportedAt).toLocaleString()}
              </span>
                        </div>
                        <div className="report-reason">{r.reason}</div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
