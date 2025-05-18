import React, { createContext, useState } from 'react';
import api from '../api/axios';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        const s = sessionStorage.getItem('user');
        return s ? JSON.parse(s) : null;
    });

    const login = async ({ email, password }) => {
        const { data } = await api.post('/auth/login', { email, password });
        sessionStorage.setItem('token', data.token);
        const u = {
            userId:   data.userId,
            username: data.username,
            email:    data.email,
            roles:    data.roles,
        };
        sessionStorage.setItem('user', JSON.stringify(u));
        setUser(u);
    };

    const register = async ({ email, username, password }) => {
        const { data } = await api.post('/auth/register', { email, username, password });
        sessionStorage.setItem('token', data.token);
        const u = {
            userId:   data.userId,
            username: data.username,
            email:    data.email,
            roles:    data.roles,
        };
        sessionStorage.setItem('user', JSON.stringify(u));
        setUser(u);
    };

    const logout = () => {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
