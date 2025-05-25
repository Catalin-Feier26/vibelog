import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import Login    from './pages/Login';
import Register from './pages/Register';
import Home     from './pages/Home';
import Profile     from './pages/Profile';
import PostsFeed    from './pages/PostsFeed';
import NewPostPage from './pages/NewPostPage';
import SinglePost  from './pages/SinglePost';
import DraftsPage  from './pages/DraftsPage';

function PrivateRoute({ children }) {
  const { user } = React.useContext(AuthContext);
  return user ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Navbar />
            <div className="main-content">
          <Routes>
            <Route path="/login"    element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={
              <PrivateRoute>
                <Home />
              </PrivateRoute>
            }/>
            <Route
                path="/profile"           // ← new protected profile route
                element={
                  <PrivateRoute>
                    <Profile />
                  </PrivateRoute>
                }
            />
            <Route path ="/posts" element={
                <PrivateRoute>
                    <PostsFeed/>
                </PrivateRoute>
                }
                />
              <Route path="/posts/new"     element={
                  <PrivateRoute>
                  <NewPostPage />
                  </PrivateRoute>
              } />
              <Route path="/posts/:postId" element={
                  <PrivateRoute>
                  <SinglePost />
                  </PrivateRoute>
              } />
              <Route path="/posts/drafts" element={
                  <PrivateRoute><DraftsPage/></PrivateRoute>
                  }/>
            {/* add more protected routes here */}
          </Routes>
            </div>
        </BrowserRouter>
      </AuthProvider>
  );
}
