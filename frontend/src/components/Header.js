import React from 'react';
import './Header.css';

const Header = ({ currentView, setCurrentView }) => {
  return (
    <header className="header">
      <div className="header-content">
        <h1>Generic Simulation</h1>
        <nav>
          <a 
            href="#dashboard" 
            className={currentView === 'dashboard' ? 'active' : ''}
            onClick={(e) => { e.preventDefault(); setCurrentView('dashboard'); }}
          >
            Dashboard
          </a>
          <a 
            href="#tribes" 
            className={currentView === 'tribes' ? 'active' : ''}
            onClick={(e) => { e.preventDefault(); setCurrentView('tribes'); }}
          >
            Tribe Statistics
          </a>
        </nav>
      </div>
    </header>
  );
};

export default Header;
