import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import TribeStatistics from './TribeStatistics';
import * as api from '../services/api';

// Mock the API module
jest.mock('../services/api');

describe('TribeStatistics', () => {
  beforeEach(() => {
    // Clear all mocks before each test
    jest.clearAllMocks();
  });

  test('displays loading state initially', () => {
    // Mock API calls
    api.getTribes.mockResolvedValue([]);
    api.getTribeStatistics.mockResolvedValue(null);

    render(<TribeStatistics />);
    expect(screen.getByText('Tribe Statistics')).toBeInTheDocument();
  });

  test('displays error when backend is not available', async () => {
    // Mock API to fail
    api.getTribes.mockRejectedValue(new Error('Connection failed'));

    render(<TribeStatistics />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to load tribes/i)).toBeInTheDocument();
    });
  });

  test('displays tribe statistics when data is loaded', async () => {
    // Mock successful API responses
    const mockTribes = [
      { tribeId: 1, tribeName: 'Northern Tribe', currentTick: 5 }
    ];

    const mockStatistics = {
      tribeId: 1,
      tribeName: 'Northern Tribe',
      currentTick: 5,
      totalPopulation: 6,
      roleBreakdown: {
        hunters: 2,
        gatherers: 2,
        children: 1,
        elders: 1
      },
      healthStats: {
        averageHealth: 96,
        minHealth: 80,
        maxHealth: 100,
        healthyMembers: 6
      },
      resourceStats: {
        food: 100,
        water: 100,
        resourceStatus: 'ABUNDANT'
      },
      policySummary: {
        foodTaxRate: 10,
        waterTaxRate: 10,
        huntingIncentive: 5,
        gatheringIncentive: 5
      }
    };

    api.getTribes.mockResolvedValue(mockTribes);
    api.getTribeStatistics.mockResolvedValue(mockStatistics);

    render(<TribeStatistics />);

    // Wait for statistics to load
    await waitFor(() => {
      expect(screen.getByText('Population Overview')).toBeInTheDocument();
    });

    // Check that key statistics are displayed
    expect(screen.getByText('Total Population')).toBeInTheDocument();
    expect(screen.getByText('Role Distribution')).toBeInTheDocument();
    expect(screen.getByText('Health Statistics')).toBeInTheDocument();
    expect(screen.getByText('Resource Status')).toBeInTheDocument();
    expect(screen.getByText('ABUNDANT')).toBeInTheDocument();
    expect(screen.getByText('Policy Summary')).toBeInTheDocument();
  });

  test('displays correct resource status descriptions', async () => {
    const mockTribes = [
      { tribeId: 1, tribeName: 'Test Tribe', currentTick: 0 }
    ];

    const mockStatistics = {
      tribeId: 1,
      tribeName: 'Test Tribe',
      currentTick: 0,
      totalPopulation: 10,
      roleBreakdown: { hunters: 5, gatherers: 3, children: 1, elders: 1 },
      healthStats: { averageHealth: 90, minHealth: 70, maxHealth: 100, healthyMembers: 10 },
      resourceStats: {
        food: 30,
        water: 30,
        resourceStatus: 'LOW'
      },
      policySummary: { foodTaxRate: 10, waterTaxRate: 10, huntingIncentive: 5, gatheringIncentive: 5 }
    };

    api.getTribes.mockResolvedValue(mockTribes);
    api.getTribeStatistics.mockResolvedValue(mockStatistics);

    render(<TribeStatistics />);

    await waitFor(() => {
      expect(screen.getByText('LOW')).toBeInTheDocument();
      expect(screen.getByText('3-4 food and water per person')).toBeInTheDocument();
    });
  });

  test('displays no tribes message when no tribes exist', async () => {
    api.getTribes.mockResolvedValue([]);

    render(<TribeStatistics />);

    await waitFor(() => {
      expect(screen.getByText(/No tribes found/i)).toBeInTheDocument();
    });
  });
});
