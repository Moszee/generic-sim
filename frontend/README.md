# Generic Simulation Frontend

React-based web UI for the Generic Simulation Engine.

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Features

- **Dashboard**: View simulation statistics and recent activity
- **Tribe Statistics View**: Comprehensive display of tribe metrics including:
  - Population overview and role distribution
  - Health statistics (average, min, max, healthy members)
  - Resource status with color-coded indicators (ABUNDANT, ADEQUATE, LOW, CRITICAL)
  - Policy summary (tax rates and incentives)
- **Tribe Selection**: Easy switching between multiple tribes
- **API Service**: Pre-configured service layer for backend integration
- **Responsive Design**: Modern, gradient-based UI that works on all devices

## Project Structure

```
frontend/
├── public/              # Static files
├── src/
│   ├── components/      # Reusable React components
│   │   └── Header.js    # Navigation header
│   ├── pages/           # Page-level components
│   │   ├── Dashboard.js        # Main dashboard with statistics
│   │   └── TribeStatistics.js  # Tribe statistics view
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
- `getTribes()` - Fetch list of all tribes
- `getTribeStatistics(tribeId)` - Fetch statistics for a specific tribe

## Using the Tribe Statistics View

1. Start the backend server (required):
   ```bash
   cd ../backend
   mvn spring-boot:run
   ```

2. Create a tribe using the backend API:
   ```bash
   curl -X POST http://localhost:8080/api/tribes \
     -H "Content-Type: application/json" \
     -d '{"name":"Northern Tribe","description":"A resilient tribe"}'
   ```

3. Navigate to the "Tribe Statistics" tab in the frontend to view detailed metrics

The statistics view displays:
- **Population Overview**: Total population and current simulation day
- **Role Distribution**: Count of hunters, gatherers, children, and elders
- **Health Statistics**: Average, minimum, maximum health, and healthy member count
- **Resource Status**: Color-coded indicator showing resource availability:
  - 🟢 **ABUNDANT** (Green): 10+ food and water per person
  - 🟡 **ADEQUATE** (Yellow): 5-9 food and water per person  
  - 🟠 **LOW** (Orange): 3-4 food and water per person
  - 🔴 **CRITICAL** (Red): Less than 3 food or water per person
- **Policy Summary**: Current tax rates and incentives

## Next Steps

- Connect the dashboard to real backend API endpoints
- Add simulation creation and management pages
- Implement user authentication
- Add real-time updates using WebSockets

## Learn More

- [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started)
- [React documentation](https://reactjs.org/)
