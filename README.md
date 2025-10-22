# Generic Simulation

A simulation engine project with a Spring Boot backend and React frontend.

## Features

### Hunter-Gatherer Tribe Simulation

The project now includes a fully functional hunter-gatherer tribe simulation with:

- **Resource Management**: Track food and water resources at tribe and family levels
- **Population Dynamics**: Manage tribe members with different roles (Hunter, Gatherer, Child, Elder)
- **Family System**: Members organized into families with independent storage
- **Skill System**: Hunting and gathering skills that improve with practice
- **Health System**: Health tracking affected by resource availability and family support
- **Sharing & Borrowing**: Families can share resources based on tribe bond level
- **Bond Mechanics**: Tribe cohesion affects cooperation and survival
- **Central Storage**: Optional tribe-level storage pool with configurable taxation
- **Storage Decay**: Realistic resource spoilage over time
- **Policy System**: Configurable rules for incentives, sharing, decay, and taxation
- **Tick-based Simulation**: Daily advancement with automated resource gathering, consumption, and aging
- **Scheduled Jobs**: Automatic daily ticks at midnight
- **REST API**: Full CRUD operations and simulation control
- **Comprehensive Tests**: 36+ unit tests covering all functionality

📖 **[View Full Documentation](wiki/Home.md)**

## Project Structure

- **backend/** - Spring Boot backend application
  - REST API for simulation management
  - Hunter-gatherer tribe simulation engine
  - H2 database (in-memory for development)
  - Health check endpoint at `/api/health`
  - Tribe simulation endpoints at `/api/tribes`

- **frontend/** - React-based web UI
  - Dashboard with simulation statistics
  - Modern, responsive design
  - Pre-configured API service layer

- **wiki/** - Comprehensive documentation
  - Simulation model details
  - API reference guide
  - Technical architecture

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

## API Examples

### Create a Tribe
```bash
curl -X POST http://localhost:8080/api/tribes \
  -H "Content-Type: application/json" \
  -d '{"name":"Northern Tribe","description":"A resilient tribe"}'
```

### Get Tribe State
```bash
curl http://localhost:8080/api/tribes/1
```

### Advance Simulation
```bash
curl -X POST http://localhost:8080/api/tribes/1/tick
```

## Documentation

See the [wiki](wiki/) for comprehensive documentation:
- [Home](wiki/Home.md) - Overview and quick start
- [Hunter-Gatherer Simulation](wiki/Hunter-Gatherer-Simulation.md) - Simulation model details
- [API Guide](wiki/API-Guide.md) - Complete API reference
- [Technical Architecture](wiki/Technical-Architecture.md) - System design and architecture