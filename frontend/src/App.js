import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './contexts/AuthContext';
import Navbar               from './components/Navbar';
import Login                from './pages/Login';
import Register             from './pages/Register';
import Home                 from './pages/Home';
import Profile              from './pages/Profile';
import PostsFeed            from './pages/PostsFeed';
import NewPostPage          from './pages/NewPostPage';
import SinglePost           from './pages/SinglePost';
import DraftsPage           from './pages/DraftsPage';
import UserProfile          from './pages/UserProfile';
import MyReports            from './pages/MyReports';
import ModerationPage       from './pages/ModerationPage';
import SearchPage           from './pages/SearchPage';
import AdminUsersPage       from './pages/AdminUsersPage';

function PrivateRoute({ children }) {
    const { user } = useContext(AuthContext);
    return user ? children : <Navigate to="/login" replace />;
}

function AppRoutes() {
    const { user } = useContext(AuthContext);

    return (
        <BrowserRouter>
            <Navbar />
            <div className="main-content">
                <Routes>
                    {/* Public */}
                    <Route path="/login"    element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Protected */}
                    <Route path="/"                element={<PrivateRoute><Home/></PrivateRoute>} />
                    <Route path="/profile"         element={<PrivateRoute><Profile/></PrivateRoute>} />
                    <Route path="/posts"           element={<PrivateRoute><PostsFeed/></PrivateRoute>} />
                    <Route path="/posts/new"       element={<PrivateRoute><NewPostPage/></PrivateRoute>} />
                    <Route path="/posts/:postId"   element={<PrivateRoute><SinglePost/></PrivateRoute>} />
                    <Route path="/posts/drafts"    element={<PrivateRoute><DraftsPage/></PrivateRoute>} />
                    <Route path="/users/:username" element={<PrivateRoute><UserProfile/></PrivateRoute>} />
                    <Route path="/reports/my"      element={<PrivateRoute><MyReports/></PrivateRoute>} />
                    <Route path="/search"          element={<PrivateRoute><SearchPage/></PrivateRoute>} />

                    {/* Moderator-only */}
                    <Route
                        path="/moderate"
                        element={
                            user?.roles.includes('MODERATOR')
                                ? <ModerationPage/>
                                : <Navigate to="/login" replace/>
                        }
                    />

                    {/* Admin-only */}
                    <Route
                        path="/admin/users"
                        element={
                            user?.roles.includes('ADMIN')
                                ? <AdminUsersPage/>
                                : <Navigate to="/" replace/>
                        }
                    />
                </Routes>
            </div>
        </BrowserRouter>
    );
}

export default function App() {
    return (
        <AuthProvider>
            <AppRoutes />
        </AuthProvider>
    );
}
