// src/pages/SearchPage.jsx
import React, { useState }       from 'react';
import { searchUsers, searchPosts } from '../api/searchService';
import PostCard                  from '../components/PostCard';
import './SearchPage.css';
import UserCard from '../components/UserCard';

export default function SearchPage() {
    const [query, setQuery]     = useState('');
    const [type, setType]       = useState('posts'); // or 'users'
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError]     = useState('');

    const handleSearch = async e => {
        e.preventDefault();
        if (!query.trim()) return;

        setLoading(true);
        setError('');
        try {
            const res = type === 'users'
                ? await searchUsers(query)
                : await searchPosts(query);
            setResults(res.data.content);
        } catch (err) {
            console.error(err);
            setError('Search failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="search-page">
            <h1>Search</h1>
            <form className="search-form" onSubmit={handleSearch}>
                <input
                    type="text"
                    className="search-input"
                    placeholder={`Search ${type}…`}
                    value={query}
                    onChange={e => setQuery(e.target.value)}
                />
                <select
                    className="search-type"
                    value={type}
                    onChange={e => setType(e.target.value)}
                >
                    <option value="posts">Posts</option>
                    <option value="users">Users</option>
                </select>
                <button type="submit" className="btn-submit">Go</button>
            </form>

            {loading && <div className="status">Searching…</div>}
            {error   && <div className="status error">{error}</div>}
            {!loading && results.length === 0 && query && (
                <div className="status empty">No results</div>
            )}

            <div className="results-list">
                {type === 'users' && results.map(u => (
                    <UserCard key={u.username} user={u} />))}

                {type === 'posts' && results.map(p => (
                    <PostCard key={p.id} post={p} />
                ))}
            </div>
        </div>
    );
}
