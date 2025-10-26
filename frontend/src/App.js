import React, { useState } from 'react';
import './App.css';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import TribeStatistics from './pages/TribeStatistics';
import TribePolicyManagement from './pages/TribePolicyManagement';

function App() {
  const [currentView, setCurrentView] = useState('dashboard');

  return (
    <div className="App">
      <Header currentView={currentView} setCurrentView={setCurrentView} />
      {currentView === 'dashboard' && <Dashboard />}
      {currentView === 'tribes' && <TribeStatistics />}
      {currentView === 'policy' && <TribePolicyManagement />}
    </div>
  );
}

export default App;
