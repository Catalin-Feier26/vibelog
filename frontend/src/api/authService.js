// src/api/authService.js
import api from './axios';

/**
 * Log in with { email, password }.
 * Returns { token, user } where:
 *  - token is the JWT
 *  - user  is the ProfileResponse/user object
 */
export const login = (credentials) =>
    api.post('/auth/login', credentials).then(res => res.data);

/**
 * Register with { email, username, password }.
 * Returns { token, user }.
 */
export const register = (credentials) =>
    api.post('/auth/register', credentials).then(res => res.data);
