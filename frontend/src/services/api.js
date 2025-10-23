// API service for backend communication
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

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
