# Generic Simulation Wiki

Welcome to the Generic Simulation project wiki!

## Overview

This project provides a flexible simulation engine with a Spring Boot backend and React frontend. The current implementation features a **Hunter-Gatherer Tribe Simulation** that models a basic tribal society with resource management, population dynamics, and policy-based governance.

## Quick Links

- **[Features Guide](Features-Guide.md)** - Comprehensive guide to tribe statistics, tick execution, and policy management
- **[Hunter-Gatherer Simulation](Hunter-Gatherer-Simulation.md)** - Detailed explanation of the simulation model and logic
- **[Generic Model Guide](Generic-Model-Guide.md)** - Guide to the extensible resource, technology, and lifestyle system
- **[API Guide](API-Guide.md)** - Complete REST API documentation with examples
- **[Technical Architecture](Technical-Architecture.md)** - System architecture and technical details
- **[Policy Engine Guide](Policy-Engine-Guide.md)** - Developer guide for extending the policy system

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 16+ (for frontend)

### Running the Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. The backend will start on `http://localhost:8080`

4. Test the health endpoint:
   ```bash
   curl http://localhost:8080/api/health
   ```

### Running the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. The frontend will open at `http://localhost:3000`

## Quick Start Example

Create and run a simple tribe simulation:

```bash
# 1. Create a new tribe
curl -X POST http://localhost:8080/api/tribes \
  -H "Content-Type: application/json" \
  -d '{"name":"My First Tribe","description":"A test tribe"}'

# 2. Get the tribe state
curl http://localhost:8080/api/tribes/1

# 3. Advance the simulation by one day
curl -X POST http://localhost:8080/api/tribes/1/tick

# 4. Check the updated state
curl http://localhost:8080/api/tribes/1
```

## Features

### Current Features
- ✅ Hunter-gatherer tribe simulation
- ✅ Generic resource system (food, water, stone, wood)
- ✅ Technology system (fire, stone tools, agriculture, animal husbandry)
- ✅ Lifestyle progression (hunter-gatherer → nomadic/settled)
- ✅ Flexible model allowing easy addition of new resources and technologies
- ✅ Population dynamics with roles (Hunter, Gatherer, Child, Elder)
- ✅ Health system
- ✅ Policy-based governance (taxes and incentives)
- ✅ Automated daily ticks
- ✅ REST API for simulation control
- ✅ H2 in-memory database
- ✅ 91 unit tests covering all functionality

### Planned Features
- 🔲 Birth and reproduction mechanics
- 🔲 Random events (droughts, abundant seasons)
- 🔲 Inter-tribe interactions
- 🔲 Resource transformation and crafting
- 🔲 Enhanced frontend visualization
- 🔲 Historical data and analytics

## Project Structure

```
generic-sim/
├── backend/           # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/genericsim/backend/
│   │   │   │       ├── controller/    # REST endpoints
│   │   │   │       ├── dto/           # Data transfer objects
│   │   │   │       ├── model/         # JPA entities
│   │   │   │       ├── repository/    # Data repositories
│   │   │   │       ├── scheduler/     # Scheduled tasks
│   │   │   │       └── service/       # Business logic
│   │   │   └── resources/
│   │   └── test/                      # Unit tests
│   └── pom.xml
├── frontend/          # React frontend
│   ├── src/
│   └── package.json
└── wiki/             # Documentation
```

## Testing

Run the test suite:

```bash
cd backend
mvn test
```

All tests should pass:
```
Tests run: 91, Failures: 0, Errors: 0, Skipped: 0
```

## Database

The application uses an H2 in-memory database for development. You can access the H2 console at:

```
http://localhost:8080/h2-console
```

**Connection details:**
- JDBC URL: `jdbc:h2:mem:genericsim`
- Username: `sa`
- Password: (empty)

## Contributing

This is an educational project. Feel free to experiment with the code and extend the simulation!

## License

See the repository LICENSE file for details.
