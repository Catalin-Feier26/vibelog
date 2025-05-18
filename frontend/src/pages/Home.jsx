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
            <h1 className="welcome-title">{welcome}</h1>
            <p className="welcome-subtitle">Your role: {user.roles.join(', ')}</p>
        </div>
    );
}
