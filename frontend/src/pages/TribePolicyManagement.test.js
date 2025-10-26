import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import TribePolicyManagement from './TribePolicyManagement';
import * as api from '../services/api';

// Mock the API module
jest.mock('../services/api');

describe('TribePolicyManagement', () => {
  const mockTribes = [
    { tribeId: 1, tribeName: 'Northern Tribe', currentTick: 5 },
    { tribeId: 2, tribeName: 'Southern Tribe', currentTick: 3 },
  ];

  const mockTribeState = {
    tribeId: 1,
    tribeName: 'Northern Tribe',
    currentTick: 5,
    policy: {
      name: 'Default Policy',
      description: 'Standard policy',
      foodTaxRate: 10,
      waterTaxRate: 10,
      huntingIncentive: 5,
      gatheringIncentive: 5,
      sharingPriority: 'ELDER',
      enableCentralStorage: false,
      centralStorageTaxRate: 10,
      storageDecayRate: 0.1,
      storageDecayInterval: 20,
    },
  };

  beforeEach(() => {
    api.getTribes.mockResolvedValue(mockTribes);
    api.getTribeState.mockResolvedValue(mockTribeState);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders component with title', async () => {
    render(<TribePolicyManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('Tribe Policy Management')).toBeInTheDocument();
    });
  });

  test('loads and displays tribes in selector', async () => {
    render(<TribePolicyManagement />);
    
    await waitFor(() => {
      const select = screen.getByLabelText(/Select Tribe:/i);
      expect(select).toBeInTheDocument();
    });
  });

  test('displays policy form sections', async () => {
    render(<TribePolicyManagement />);
    
    await waitFor(() => {
      expect(screen.getByText('Tax Rates')).toBeInTheDocument();
      expect(screen.getByText('Gathering Incentives')).toBeInTheDocument();
      expect(screen.getByText('Resource Sharing')).toBeInTheDocument();
      expect(screen.getByText('Central Storage')).toBeInTheDocument();
      expect(screen.getByText('Storage Decay')).toBeInTheDocument();
    });
  });

  test('displays error message when tribes fail to load', async () => {
    api.getTribes.mockRejectedValue(new Error('Failed to load'));
    
    render(<TribePolicyManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(/Failed to load tribes/i)).toBeInTheDocument();
    });
  });

  test('displays no tribes message when no tribes exist', async () => {
    api.getTribes.mockResolvedValue([]);
    
    render(<TribePolicyManagement />);
    
    await waitFor(() => {
      expect(screen.getByText(/No tribes found/i)).toBeInTheDocument();
    });
  });
});
