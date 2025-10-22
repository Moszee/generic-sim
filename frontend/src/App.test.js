import { render, screen } from '@testing-library/react';
import App from './App';

test('renders dashboard heading', () => {
  render(<App />);
  const headingElement = screen.getByText(/Generic Simulation Dashboard/i);
  expect(headingElement).toBeInTheDocument();
});

test('renders active simulations stat', () => {
  render(<App />);
  const statElement = screen.getByText(/Active Simulations/i);
  expect(statElement).toBeInTheDocument();
});
