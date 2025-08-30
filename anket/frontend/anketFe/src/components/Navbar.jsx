import React from 'react';
import './Navbar.css';
import { Link, useNavigate } from 'react-router-dom';
import { getStoredUser } from '../services/api';

const Navbar = ({ visibleButtons = [] }) => {
  const navigate = useNavigate();
  const currentUser = getStoredUser();
  const isAdmin = currentUser && currentUser.role === 'ADMIN';

  const handleLogout = () => {
    navigate('/'); // logout sonrası yönlendirme
  };

  const allButtons = {
    profile: (
      <Link to="/profilepage" className="navbar-btn profile-btn">
        Profile
      </Link>
    ),
    admin: isAdmin ? (
      <Link to="/admin" className="navbar-btn admin-btn">
        Admin
      </Link>
    ) : null,
    logout: (
      <button onClick={handleLogout} className="navbar-btn logout-btn">
        Logout
      </button>
    ),
  };

  // visibleButtons'ın dizi olduğundan emin ol
  const safeButtons = Array.isArray(visibleButtons) ? visibleButtons : [];

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-content">
          <div>
            <Link to="/homepage" className="navbar-brand">
              Survet
            </Link>
          </div>
          <div className="navbar-right">
            {safeButtons.map((btn) => {
              const buttonComponent = allButtons[btn];
              return buttonComponent ? (
                <React.Fragment key={btn}>
                  {buttonComponent}
                </React.Fragment>
              ) : null;
            })}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
