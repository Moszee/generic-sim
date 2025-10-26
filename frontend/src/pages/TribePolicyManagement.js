import React, { useState, useEffect } from 'react';
import { getTribeState, getTribes, updateTribePolicy } from '../services/api';
import './TribePolicyManagement.css';

const TribePolicyManagement = () => {
  const [tribes, setTribes] = useState([]);
  const [selectedTribeId, setSelectedTribeId] = useState(null);
  const [currentPolicy, setCurrentPolicy] = useState(null);
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

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

  // Load tribe state when a tribe is selected
  useEffect(() => {
    if (selectedTribeId) {
      loadTribeState(selectedTribeId);
    }
  }, [selectedTribeId]);

  const loadTribeState = async (tribeId) => {
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    try {
      const state = await getTribeState(tribeId);
      setCurrentPolicy(state.policy);
      // Initialize form with current policy values
      setFormData({
        foodTaxRate: state.policy.foodTaxRate,
        waterTaxRate: state.policy.waterTaxRate,
        huntingIncentive: state.policy.huntingIncentive,
        gatheringIncentive: state.policy.gatheringIncentive,
        sharingPriority: state.policy.sharingPriority,
        enableCentralStorage: state.policy.enableCentralStorage,
        centralStorageTaxRate: state.policy.centralStorageTaxRate,
        storageDecayRate: state.policy.storageDecayRate,
        storageDecayInterval: state.policy.storageDecayInterval,
      });
    } catch (err) {
      setError('Failed to load tribe policy');
    } finally {
      setLoading(false);
    }
  };

  const handleTribeChange = (e) => {
    setSelectedTribeId(Number(e.target.value));
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : type === 'number' ? Number(value) : value,
    });
  };

  const handleReset = () => {
    if (currentPolicy) {
      setFormData({
        foodTaxRate: currentPolicy.foodTaxRate,
        waterTaxRate: currentPolicy.waterTaxRate,
        huntingIncentive: currentPolicy.huntingIncentive,
        gatheringIncentive: currentPolicy.gatheringIncentive,
        sharingPriority: currentPolicy.sharingPriority,
        enableCentralStorage: currentPolicy.enableCentralStorage,
        centralStorageTaxRate: currentPolicy.centralStorageTaxRate,
        storageDecayRate: currentPolicy.storageDecayRate,
        storageDecayInterval: currentPolicy.storageDecayInterval,
      });
      setSuccessMessage(null);
      setError(null);
    }
  };

  const validateFormData = () => {
    const errors = [];
    
    if (formData.foodTaxRate < 0 || formData.foodTaxRate > 100) {
      errors.push('Food tax rate must be between 0 and 100');
    }
    if (formData.waterTaxRate < 0 || formData.waterTaxRate > 100) {
      errors.push('Water tax rate must be between 0 and 100');
    }
    if (formData.huntingIncentive < 0 || formData.huntingIncentive > 100) {
      errors.push('Hunting incentive must be between 0 and 100');
    }
    if (formData.gatheringIncentive < 0 || formData.gatheringIncentive > 100) {
      errors.push('Gathering incentive must be between 0 and 100');
    }
    if (formData.centralStorageTaxRate < 0 || formData.centralStorageTaxRate > 100) {
      errors.push('Central storage tax rate must be between 0 and 100');
    }
    if (formData.storageDecayRate < 0 || formData.storageDecayRate > 1) {
      errors.push('Storage decay rate must be between 0 and 1');
    }
    if (formData.storageDecayInterval < 1) {
      errors.push('Storage decay interval must be at least 1');
    }
    
    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage(null);
    
    // Validate form data
    const validationErrors = validateFormData();
    if (validationErrors.length > 0) {
      setError(validationErrors.join('. '));
      return;
    }
    
    setLoading(true);
    try {
      await updateTribePolicy(selectedTribeId, formData);
      setSuccessMessage('Policy updated successfully!');
      // Reload the tribe state to reflect changes
      await loadTribeState(selectedTribeId);
    } catch (err) {
      setError('Failed to update policy. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const hasChanges = () => {
    if (!currentPolicy) return false;
    return Object.keys(formData).some(key => {
      return formData[key] !== currentPolicy[key];
    });
  };

  if (error && tribes.length === 0) {
    return (
      <div className="policy-management">
        <h1>Tribe Policy Management</h1>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="policy-management">
      <h1>Tribe Policy Management</h1>
      <p className="page-description">
        Configure policy settings to control how your tribe operates. 
        Adjust tax rates, incentives, resource sharing, and storage settings.
      </p>

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

      {loading && <div className="loading">Loading...</div>}

      {successMessage && (
        <div className="success-message">{successMessage}</div>
      )}

      {error && <div className="error-message">{error}</div>}

      {currentPolicy && !loading && (
        <form onSubmit={handleSubmit} className="policy-form">
          <div className="form-section">
            <h2>Tax Rates</h2>
            <p className="section-description">
              Percentage of resources collected as tax for tribe storage
            </p>
            
            <div className="form-group">
              <label htmlFor="foodTaxRate">
                Food Tax Rate (%)
                <span className="current-value">Current: {currentPolicy.foodTaxRate}%</span>
              </label>
              <input
                type="number"
                id="foodTaxRate"
                name="foodTaxRate"
                min="0"
                max="100"
                value={formData.foodTaxRate}
                onChange={handleInputChange}
                className={formData.foodTaxRate !== currentPolicy.foodTaxRate ? 'changed' : ''}
              />
            </div>

            <div className="form-group">
              <label htmlFor="waterTaxRate">
                Water Tax Rate (%)
                <span className="current-value">Current: {currentPolicy.waterTaxRate}%</span>
              </label>
              <input
                type="number"
                id="waterTaxRate"
                name="waterTaxRate"
                min="0"
                max="100"
                value={formData.waterTaxRate}
                onChange={handleInputChange}
                className={formData.waterTaxRate !== currentPolicy.waterTaxRate ? 'changed' : ''}
              />
            </div>
          </div>

          <div className="form-section">
            <h2>Gathering Incentives</h2>
            <p className="section-description">
              Bonus resources awarded to encourage hunting and gathering activities
            </p>
            
            <div className="form-group">
              <label htmlFor="huntingIncentive">
                Hunting Incentive
                <span className="current-value">Current: {currentPolicy.huntingIncentive}</span>
              </label>
              <input
                type="number"
                id="huntingIncentive"
                name="huntingIncentive"
                min="0"
                max="100"
                value={formData.huntingIncentive}
                onChange={handleInputChange}
                className={formData.huntingIncentive !== currentPolicy.huntingIncentive ? 'changed' : ''}
              />
            </div>

            <div className="form-group">
              <label htmlFor="gatheringIncentive">
                Gathering Incentive
                <span className="current-value">Current: {currentPolicy.gatheringIncentive}</span>
              </label>
              <input
                type="number"
                id="gatheringIncentive"
                name="gatheringIncentive"
                min="0"
                max="100"
                value={formData.gatheringIncentive}
                onChange={handleInputChange}
                className={formData.gatheringIncentive !== currentPolicy.gatheringIncentive ? 'changed' : ''}
              />
            </div>
          </div>

          <div className="form-section">
            <h2>Resource Sharing</h2>
            <p className="section-description">
              Determine who receives priority when resources are shared among families
            </p>
            
            <div className="form-group">
              <label htmlFor="sharingPriority">
                Sharing Priority
                <span className="current-value">Current: {currentPolicy.sharingPriority}</span>
              </label>
              <select
                id="sharingPriority"
                name="sharingPriority"
                value={formData.sharingPriority}
                onChange={handleInputChange}
                className={formData.sharingPriority !== currentPolicy.sharingPriority ? 'changed' : ''}
              >
                <option value="ELDER">Elder - Prioritize oldest members</option>
                <option value="CHILD">Child - Prioritize youngest members</option>
                <option value="HUNTER">Hunter - Prioritize hunters</option>
                <option value="GATHERER">Gatherer - Prioritize gatherers</option>
                <option value="YOUNGEST">Youngest - Prioritize by age (youngest first)</option>
                <option value="RANDOM">Random - No priority order</option>
              </select>
            </div>
          </div>

          <div className="form-section">
            <h2>Central Storage</h2>
            <p className="section-description">
              Enable tribe-level storage pool with additional taxation
            </p>
            
            <div className="form-group checkbox-group">
              <label htmlFor="enableCentralStorage">
                <input
                  type="checkbox"
                  id="enableCentralStorage"
                  name="enableCentralStorage"
                  checked={formData.enableCentralStorage}
                  onChange={handleInputChange}
                  className={formData.enableCentralStorage !== currentPolicy.enableCentralStorage ? 'changed' : ''}
                />
                Enable Central Storage
                <span className="current-value">
                  Current: {currentPolicy.enableCentralStorage ? 'Enabled' : 'Disabled'}
                </span>
              </label>
            </div>

            <div className="form-group">
              <label htmlFor="centralStorageTaxRate">
                Central Storage Tax Rate (%)
                <span className="current-value">Current: {currentPolicy.centralStorageTaxRate}%</span>
              </label>
              <input
                type="number"
                id="centralStorageTaxRate"
                name="centralStorageTaxRate"
                min="0"
                max="100"
                value={formData.centralStorageTaxRate}
                onChange={handleInputChange}
                disabled={!formData.enableCentralStorage}
                className={formData.centralStorageTaxRate !== currentPolicy.centralStorageTaxRate ? 'changed' : ''}
              />
            </div>
          </div>

          <div className="form-section">
            <h2>Storage Decay</h2>
            <p className="section-description">
              Configure how stored resources spoil over time
            </p>
            
            <div className="form-group">
              <label htmlFor="storageDecayRate">
                Storage Decay Rate (0-1)
                <span className="current-value">Current: {currentPolicy.storageDecayRate}</span>
              </label>
              <input
                type="number"
                id="storageDecayRate"
                name="storageDecayRate"
                min="0"
                max="1"
                step="0.01"
                value={formData.storageDecayRate}
                onChange={handleInputChange}
                className={formData.storageDecayRate !== currentPolicy.storageDecayRate ? 'changed' : ''}
              />
              <span className="field-hint">
                Fraction of stored resources lost per decay interval (e.g., 0.1 = 10% loss)
              </span>
            </div>

            <div className="form-group">
              <label htmlFor="storageDecayInterval">
                Storage Decay Interval (days)
                <span className="current-value">Current: {currentPolicy.storageDecayInterval}</span>
              </label>
              <input
                type="number"
                id="storageDecayInterval"
                name="storageDecayInterval"
                min="1"
                value={formData.storageDecayInterval}
                onChange={handleInputChange}
                className={formData.storageDecayInterval !== currentPolicy.storageDecayInterval ? 'changed' : ''}
              />
              <span className="field-hint">
                Number of days between each decay event
              </span>
            </div>
          </div>

          <div className="form-actions">
            <button type="button" onClick={handleReset} className="btn-reset">
              Reset Changes
            </button>
            <button 
              type="submit" 
              className="btn-submit" 
              disabled={!hasChanges() || loading}
            >
              {loading ? 'Updating...' : 'Update Policy'}
            </button>
          </div>

          {hasChanges() && (
            <div className="changes-notice">
              You have unsaved changes. Click "Update Policy" to save or "Reset Changes" to discard.
            </div>
          )}
        </form>
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

export default TribePolicyManagement;
