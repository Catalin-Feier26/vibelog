import React, { useState, useEffect } from 'react';
import { getNotificationCount, markAllNotificationsRead } from '../api/notificationService';
import NotificationDropdown from './NotificationDropdown';
import './NotificationBell.css';

export default function NotificationBell() {
    const [count, setCount] = useState(0);
    const [open, setOpen]   = useState(false);

    useEffect(() => {
        let mounted = true;
        const load = async () => {
            try {
                const res = await getNotificationCount();
                if (mounted) setCount(res.data.count);
            } catch {}
        };
        load();
        const iv = setInterval(load, 30000);
        return () => {
            mounted = false;
            clearInterval(iv);
        };
    }, []);

    const toggle = () => {
        const next = !open;
        setOpen(next);
        if (next && count > 0) {
            markAllNotificationsRead().then(() => setCount(0)).catch();
        }
    };

    return (
        <div className="notification-bell">
            <button className="bell-button" onClick={toggle}>
                🔔
                {count > 0 && <span className="badge">{count}</span>}
            </button>
            {open && <NotificationDropdown onClose={() => setOpen(false)} />}
        </div>
    );
}
