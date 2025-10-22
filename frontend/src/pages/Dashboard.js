import React from 'react';
import './Dashboard.css';

const Dashboard = () => {
  // Mock data for statistics
  const stats = {
    activeSimulations: 12,
    totalSimulations: 45,
    avgExecutionTime: '3.2s',
    successRate: '94.5%'
  };

  return (
    <div className="dashboard">
      <h1>Generic Simulation Dashboard</h1>
      <div className="stats-container">
        <div className="stat-card">
          <h3>Active Simulations</h3>
          <p className="stat-value">{stats.activeSimulations}</p>
        </div>
        <div className="stat-card">
          <h3>Total Simulations</h3>
          <p className="stat-value">{stats.totalSimulations}</p>
        </div>
        <div className="stat-card">
          <h3>Avg Execution Time</h3>
          <p className="stat-value">{stats.avgExecutionTime}</p>
        </div>
        <div className="stat-card">
          <h3>Success Rate</h3>
          <p className="stat-value">{stats.successRate}</p>
        </div>
      </div>
      <div className="info-section">
        <h2>Recent Activity</h2>
        <ul className="activity-list">
          <li>Simulation #45 completed successfully (2 min ago)</li>
          <li>Simulation #44 completed successfully (15 min ago)</li>
          <li>Simulation #43 completed successfully (32 min ago)</li>
          <li>Simulation #42 failed (1 hour ago)</li>
          <li>Simulation #41 completed successfully (2 hours ago)</li>
        </ul>
      </div>
    </div>
  );
};

export default Dashboard;
