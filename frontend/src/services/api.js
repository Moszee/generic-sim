// API service for backend communication
const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

/**
 * Fetch simulation statistics from the backend
 * @returns {Promise<Object>} Statistics data
 */
export const getSimulationStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/simulations/stats`);
    if (!response.ok) {
      throw new Error('Failed to fetch simulation stats');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching simulation stats:', error);
    throw error;
  }
};

/**
 * Fetch list of simulations from the backend
 * @returns {Promise<Array>} List of simulations
 */
export const getSimulations = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/simulations`);
    if (!response.ok) {
      throw new Error('Failed to fetch simulations');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching simulations:', error);
    throw error;
  }
};

/**
 * Check backend health
 * @returns {Promise<Object>} Health status
 */
export const checkHealth = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/health`);
    if (!response.ok) {
      throw new Error('Failed to check health');
    }
    return await response.json();
  } catch (error) {
    console.error('Error checking health:', error);
    throw error;
  }
};

/**
 * Fetch all tribes
 * @returns {Promise<Array>} List of tribes
 */
export const getTribes = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/tribes`);
    if (!response.ok) {
      throw new Error('Failed to fetch tribes');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching tribes:', error);
    throw error;
  }
};

/**
 * Fetch statistics for a specific tribe
 * @param {number} tribeId - The ID of the tribe
 * @returns {Promise<Object>} Tribe statistics
 */
export const getTribeStatistics = async (tribeId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/tribes/${tribeId}/statistics`);
    if (!response.ok) {
      throw new Error('Failed to fetch tribe statistics');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching tribe statistics:', error);
    throw error;
  }
};

/**
 * Fetch full state for a specific tribe (includes policy details)
 * @param {number} tribeId - The ID of the tribe
 * @returns {Promise<Object>} Tribe state with full policy details
 */
export const getTribeState = async (tribeId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/tribes/${tribeId}`);
    if (!response.ok) {
      throw new Error('Failed to fetch tribe state');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching tribe state:', error);
    throw error;
  }
};

/**
 * Update tribe policy settings
 * @param {number} tribeId - The ID of the tribe
 * @param {Object} policyUpdate - Policy fields to update (partial update supported)
 * @returns {Promise<Object>} Updated tribe state
 */
export const updateTribePolicy = async (tribeId, policyUpdate) => {
  try {
    const response = await fetch(`${API_BASE_URL}/tribes/${tribeId}/policy`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(policyUpdate),
    });
    if (!response.ok) {
      throw new Error('Failed to update tribe policy');
    }
    return await response.json();
  } catch (error) {
    console.error('Error updating tribe policy:', error);
    throw error;
  }
};
