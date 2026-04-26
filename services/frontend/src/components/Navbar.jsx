import React from 'react';
import { useNavigate } from 'react-router-dom';

function Navbar() {
  const navigate = useNavigate();
  const email = localStorage.getItem('userEmail');

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '12px 24px',
      borderBottom: '1px solid var(--border)',
      backgroundColor: 'var(--bg)'
    }}>
      <h2 style={{ margin: 0, cursor: 'pointer' }} onClick={() => navigate('/')}>
        🧠 Эффективность+
      </h2>
      <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
        <span>{email || 'Пользователь'}</span>
        <button onClick={handleLogout} style={{
          padding: '6px 12px',
          background: 'var(--accent)',
          color: 'white',
          border: 'none',
          borderRadius: '6px',
          cursor: 'pointer'
        }}>
          Выйти
        </button>
      </div>
    </div>
  );
}

export default Navbar;