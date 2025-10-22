# Generic Simulation Frontend

React-based web UI for the Generic Simulation Engine.

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Features

- **Dashboard**: View simulation statistics and recent activity
- **Mock Data**: Currently displays static data for demonstration
- **API Service**: Pre-configured service layer for backend integration
- **Responsive Design**: Modern, gradient-based UI design

## Project Structure

```
frontend/
├── public/              # Static files
├── src/
│   ├── components/      # Reusable React components
│   │   └── Header.js    # Navigation header
│   ├── pages/           # Page-level components
│   │   └── Dashboard.js # Main dashboard with statistics
│   ├── services/        # API service layer
│   │   └── api.js       # Backend API calls
│   ├── App.js           # Main app component
│   └── index.js         # Entry point
└── package.json
```

## Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)

## Getting Started

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

### Running Locally

Start the development server:

```bash
npm start
```

The app will open at [http://localhost:3000](http://localhost:3000) in your browser.

The page will automatically reload when you make changes.

### Building for Production

Build the app for production:

```bash
npm run build
```

This creates an optimized production build in the `build/` folder.

### Running Tests

Launch the test runner in interactive watch mode:

```bash
npm test
```

## API Integration

The frontend is pre-configured to connect to the backend API. By default, it expects the backend to run at `http://localhost:8080/api`.

To configure a different backend URL, create a `.env` file in the frontend directory:

```
REACT_APP_API_URL=http://your-backend-url/api
```

## Available API Services

The `services/api.js` file includes:

- `getSimulationStats()` - Fetch simulation statistics
- `getSimulations()` - Fetch list of simulations
- `checkHealth()` - Check backend health status

## Next Steps

- Connect the dashboard to real backend API endpoints
- Add simulation creation and management pages
- Implement user authentication
- Add real-time updates using WebSockets

## Learn More

- [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started)
- [React documentation](https://reactjs.org/)
