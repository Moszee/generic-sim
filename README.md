# Generic Simulation

A simulation engine project with a Spring Boot backend and React frontend.

## Project Structure

- **backend/** - Spring Boot backend application
  - REST API for simulation management
  - H2 database (in-memory for development)
  - Health check endpoint at `/api/health`

- **frontend/** - React-based web UI
  - Dashboard with simulation statistics
  - Modern, responsive design
  - Pre-configured API service layer

## Getting Started

### Backend

See the [backend README](backend/README.md) for detailed setup instructions.

```bash
cd backend
mvn spring-boot:run
```

The backend will run at `http://localhost:8080`

### Frontend

See the [frontend README](frontend/README.md) for detailed setup instructions.

```bash
cd frontend
npm install
npm start
```

The frontend will run at `http://localhost:3000`

## Quick Start

1. Start the backend server:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. In a new terminal, start the frontend:
   ```bash
   cd frontend
   npm start
   ```

3. Visit `http://localhost:3000` to view the application

## Development

- Backend runs on port 8080
- Frontend runs on port 3000
- Frontend is configured to proxy API requests to the backend