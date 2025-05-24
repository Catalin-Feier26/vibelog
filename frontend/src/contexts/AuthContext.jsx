// src/contexts/AuthContext.jsx
import React, { createContext, useState } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/authService';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    // Load and parse stored user, but guard against "undefined" or bad JSON
    const [user, setUser] = useState(() => {
        const stored = sessionStorage.getItem('user');
        if (!stored || stored === 'undefined') return null;
        try {
            return JSON.parse(stored);
        } catch {
            return null;
        }
    });

    /** Overwrite the JWT in sessionStorage */
    const setToken = (token) => {
        sessionStorage.setItem('token', token);
    };

    /**
     * Log in with { email, password }.
     * Expects AuthResponse: { token, tokenType, expiresAt, userId, username, email, roles }
     * Builds a simpler user object and persists both token + user.
     */
    const login = async (creds) => {
        const resp = await apiLogin(creds);
        const { token, userId, username, email, roles } = resp;

        const u = { id: userId, username, email, roles };
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('user', JSON.stringify(u));
        setUser(u);
        return u;
    };

    /**
     * Register with { email, username, password }.
     * Same shape as login.
     */
    const register = async (creds) => {
        const resp = await apiRegister(creds);
        const { token, userId, username, email, roles } = resp;

        const u = { id: userId, username, email, roles };
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('user', JSON.stringify(u));
        setUser(u);
        return u;
    };

    /** Clear out auth data */
    const logout = () => {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, setUser, setToken, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
