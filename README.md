# Generic Simulation

A simulation engine project with a Spring Boot backend.

## Project Structure

- **backend/** - Spring Boot backend application
  - REST API for simulation management
  - H2 database (in-memory for development)
  - Health check endpoint at `/api/health`

## Getting Started

See the [backend README](backend/README.md) for detailed setup instructions.

## Quick Start

```bash
cd backend
mvn spring-boot:run
```

Visit `http://localhost:8080/api/health` to verify the application is running.