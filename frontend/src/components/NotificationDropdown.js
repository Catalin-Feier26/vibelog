import React, { useState, useEffect } from 'react';
import {
    getNotifications,
    markNotificationRead,
    markAllNotificationsRead
} from '../api/notificationService';
import './NotificationDropdown.css';

export default function NotificationDropdown({ onClose }) {
    const [notes, setNotes] = useState([]);

    useEffect(() => {
        (async () => {
            try {
                const res = await getNotifications(0, 20);
                setNotes(res.data.content);
            } catch {}
        })();
    }, []);

    const handleReadOne = async id => {
        try {
            await markNotificationRead(id);
            setNotes(ns => ns.map(n => n.id === id ? { ...n, seen: true } : n));
        } catch {}
    };

    const handleReadAll = async () => {
        try {
            await markAllNotificationsRead();
            setNotes(ns => ns.map(n => ({ ...n, seen: true })));
        } catch {}
    };

    return (
        <div className="notification-dropdown animate-fadein">
            <div className="dropdown-header animate-fadeup">
                <span className="header-title">Notifications</span>
                <div className="header-actions">
                    <button onClick={handleReadAll} className="btn-read-all">
                        Mark All Read
                    </button>
                    <button onClick={onClose} className="btn-close">
                        ✖
                    </button>
                </div>
            </div>
            <ul className="dropdown-list">
                {notes.length === 0 && (
                    <li className="empty">No notifications.</li>
                )}
                {notes.map(n => (
                    <li key={n.id} className={n.seen ? 'seen' : 'unseen'}>
                        <div
                            className="notification-item"
                            onClick={() => handleReadOne(n.id)}
                        >
                            <p className="notif-content">{n.content}</p>
                            <small className="notif-time">
                                {new Date(n.timestamp).toLocaleString()}
                            </small>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
}
