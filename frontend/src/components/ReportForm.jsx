import React, { useState } from 'react';
import { reportPost, reportComment } from '../api/reportService';
import './ReportForm.css';

export default function ReportForm({ targetId, type, onCancel, onReported }) {
    // type === 'post' or 'comment'
    const [reason, setReason] = useState('');
    const [error, setError]   = useState('');

    const handleSubmit = async e => {
        e.preventDefault();
        setError('');
        try {
            if (type === 'post') {
                await reportPost(targetId, reason);
            } else {
                await reportComment(targetId, reason);
            }
            onReported && onReported();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to submit report');
        }
    };

    return (
        <form className="report-form" onSubmit={handleSubmit}>
            {error && <div className="report-error">{error}</div>}
            <textarea
                value={reason}
                onChange={e => setReason(e.target.value)}
                placeholder="Tell us why you’re reporting…"
                rows={3}
                required
            />
            <div className="report-actions">
                <button type="submit" className="btn-report-submit">
                    Send Report
                </button>
                <button type="button" onClick={onCancel} className="btn-report-cancel">
                    Cancel
                </button>
            </div>
        </form>
    );
}
