import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import Login    from './pages/Login';
import Register from './pages/Register';
import Home     from './pages/Home';
import Profile     from './pages/Profile';
// import Profile, AdminPanel, Moderate pages as you build them

function PrivateRoute({ children }) {
  const { user } = React.useContext(AuthContext);
  return user ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Navbar />
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
            {/* add more protected routes here */}
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}
