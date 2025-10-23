import React, { useState, useEffect } from 'react';
import { getTribeStatistics, getTribes } from '../services/api';
import './TribeStatistics.css';

const TribeStatistics = () => {
  const [tribes, setTribes] = useState([]);
  const [selectedTribeId, setSelectedTribeId] = useState(null);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Load available tribes on component mount
  useEffect(() => {
    const loadTribes = async () => {
      try {
        const tribesData = await getTribes();
        setTribes(tribesData);
        // Auto-select first tribe if available
        if (tribesData.length > 0 && !selectedTribeId) {
          setSelectedTribeId(tribesData[0].tribeId);
        }
      } catch (err) {
        setError('Failed to load tribes. Make sure the backend is running.');
      }
    };
    loadTribes();
  }, [selectedTribeId]);

  // Load statistics when a tribe is selected
  useEffect(() => {
    if (selectedTribeId) {
      loadStatistics(selectedTribeId);
    }
  }, [selectedTribeId]);

  const loadStatistics = async (tribeId) => {
    setLoading(true);
    setError(null);
    try {
      const stats = await getTribeStatistics(tribeId);
      setStatistics(stats);
    } catch (err) {
      setError('Failed to load tribe statistics');
    } finally {
      setLoading(false);
    }
  };

  const handleTribeChange = (e) => {
    setSelectedTribeId(Number(e.target.value));
  };

  const getResourceStatusClass = (status) => {
    switch (status) {
      case 'ABUNDANT': return 'status-abundant';
      case 'ADEQUATE': return 'status-adequate';
      case 'LOW': return 'status-low';
      case 'CRITICAL': return 'status-critical';
      default: return '';
    }
  };

  const getResourceStatusDescription = (status) => {
    switch (status) {
      case 'ABUNDANT': return '10+ food and water per person';
      case 'ADEQUATE': return '5-9 food and water per person';
      case 'LOW': return '3-4 food and water per person';
      case 'CRITICAL': return 'Less than 3 food or water per person';
      default: return 'Unknown';
    }
  };

  if (error && tribes.length === 0) {
    return (
      <div className="tribe-statistics">
        <h1>Tribe Statistics</h1>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="tribe-statistics">
      <h1>Tribe Statistics</h1>
      
      {tribes.length > 0 && (
        <div className="tribe-selector">
          <label htmlFor="tribe-select">Select Tribe: </label>
          <select id="tribe-select" value={selectedTribeId || ''} onChange={handleTribeChange}>
            {tribes.map(tribe => (
              <option key={tribe.tribeId} value={tribe.tribeId}>
                {tribe.tribeName} (Day {tribe.currentTick})
              </option>
            ))}
          </select>
        </div>
      )}

      {loading && <div className="loading">Loading statistics...</div>}
      
      {error && <div className="error-message">{error}</div>}

      {statistics && !loading && (
        <div className="statistics-container">
          {/* Population Section */}
          <div className="stats-section">
            <h2>Population Overview</h2>
            <div className="stats-grid">
              <div className="stat-card">
                <h3>Total Population</h3>
                <p className="stat-value">{statistics.totalPopulation}</p>
              </div>
              <div className="stat-card">
                <h3>Current Day</h3>
                <p className="stat-value">{statistics.currentTick}</p>
              </div>
            </div>
          </div>

          {/* Role Breakdown Section */}
          <div className="stats-section">
            <h2>Role Distribution</h2>
            <div className="stats-grid">
              <div className="stat-card role-hunter">
                <h3>Hunters</h3>
                <p className="stat-value">{statistics.roleBreakdown.hunters}</p>
              </div>
              <div className="stat-card role-gatherer">
                <h3>Gatherers</h3>
                <p className="stat-value">{statistics.roleBreakdown.gatherers}</p>
              </div>
              <div className="stat-card role-child">
                <h3>Children</h3>
                <p className="stat-value">{statistics.roleBreakdown.children}</p>
              </div>
              <div className="stat-card role-elder">
                <h3>Elders</h3>
                <p className="stat-value">{statistics.roleBreakdown.elders}</p>
              </div>
            </div>
          </div>

          {/* Health Statistics Section */}
          <div className="stats-section">
            <h2>Health Statistics</h2>
            <div className="stats-grid">
              <div className="stat-card health-avg">
                <h3>Average Health</h3>
                <p className="stat-value">{statistics.healthStats.averageHealth}</p>
              </div>
              <div className="stat-card health-min">
                <h3>Minimum Health</h3>
                <p className="stat-value">{statistics.healthStats.minHealth}</p>
              </div>
              <div className="stat-card health-max">
                <h3>Maximum Health</h3>
                <p className="stat-value">{statistics.healthStats.maxHealth}</p>
              </div>
              <div className="stat-card health-healthy">
                <h3>Healthy Members</h3>
                <p className="stat-value">{statistics.healthStats.healthyMembers}</p>
                <p className="stat-subtitle">Health ≥ 70</p>
              </div>
            </div>
          </div>

          {/* Resource Status Section */}
          <div className="stats-section">
            <h2>Resource Status</h2>
            <div className="resource-status-container">
              <div className={`resource-status-badge ${getResourceStatusClass(statistics.resourceStats.resourceStatus)}`}>
                <h3>{statistics.resourceStats.resourceStatus}</h3>
                <p>{getResourceStatusDescription(statistics.resourceStats.resourceStatus)}</p>
              </div>
            </div>
            <div className="stats-grid">
              <div className="stat-card resource-food">
                <h3>Total Food</h3>
                <p className="stat-value">{statistics.resourceStats.food}</p>
                <p className="stat-subtitle">
                  {statistics.totalPopulation > 0 
                    ? `${Math.floor(statistics.resourceStats.food / statistics.totalPopulation)} per person`
                    : 'No members'}
                </p>
              </div>
              <div className="stat-card resource-water">
                <h3>Total Water</h3>
                <p className="stat-value">{statistics.resourceStats.water}</p>
                <p className="stat-subtitle">
                  {statistics.totalPopulation > 0 
                    ? `${Math.floor(statistics.resourceStats.water / statistics.totalPopulation)} per person`
                    : 'No members'}
                </p>
              </div>
            </div>
          </div>

          {/* Policy Summary Section */}
          {statistics.policySummary && (
            <div className="stats-section">
              <h2>Policy Summary</h2>
              <div className="stats-grid">
                <div className="stat-card policy-tax">
                  <h3>Food Tax Rate</h3>
                  <p className="stat-value">{statistics.policySummary.foodTaxRate}%</p>
                </div>
                <div className="stat-card policy-tax">
                  <h3>Water Tax Rate</h3>
                  <p className="stat-value">{statistics.policySummary.waterTaxRate}%</p>
                </div>
                <div className="stat-card policy-incentive">
                  <h3>Hunting Incentive</h3>
                  <p className="stat-value">{statistics.policySummary.huntingIncentive}</p>
                </div>
                <div className="stat-card policy-incentive">
                  <h3>Gathering Incentive</h3>
                  <p className="stat-value">{statistics.policySummary.gatheringIncentive}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {tribes.length === 0 && !loading && !error && (
        <div className="no-tribes-message">
          <p>No tribes found. Create a tribe using the backend API first.</p>
          <pre>
            curl -X POST http://localhost:8080/api/tribes \
              -H "Content-Type: application/json" \
              -d '{'{'}
                "name":"Northern Tribe",
                "description":"A resilient tribe"
              {'}'}'
          </pre>
        </div>
      )}
    </div>
  );
};

export default TribeStatistics;
