import React from 'react';
import './Header.css';

const Header = () => {
  return (
    <header className="header">
      <div className="header-content">
        <h1>Generic Simulation</h1>
        <nav>
          <a href="/">Dashboard</a>
        </nav>
      </div>
    </header>
  );
};

export default Header;
