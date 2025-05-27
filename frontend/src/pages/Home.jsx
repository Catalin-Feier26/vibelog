import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import './Home.css';

export default function Home() {
    const { user } = useContext(AuthContext);
    const [welcome, setWelcome] = useState('');

    useEffect(() => {
        setWelcome(`Welcome back, ${user.username}!`);
    }, [user]);

    return (
        <div className="home-page">
            <div className="welcome-card">
                <h1 className="welcome-title">{welcome}</h1>
                <div className="welcome-subtitle">
                    Your role:
                    {user.roles.map(role => (
                        <span key={role} className="role-badge">{role}</span>
                    ))}
                </div>
            </div>
        </div>
    );
}
