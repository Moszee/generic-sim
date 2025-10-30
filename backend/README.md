# Generic Simulation Backend

This is the backend simulation engine for the Generic Simulation project, built with Spring Boot.

## Technologies

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Web** - REST API framework
- **Spring Data JPA** - Data persistence layer
- **H2 Database** - In-memory database (for MVP, can be switched to PostgreSQL later)
- **Maven** - Build and dependency management

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/genericsim/backend/
│   │   │   ├── controller/     # REST API controllers
│   │   │   ├── service/        # Business logic services
│   │   │   ├── model/          # JPA entities
│   │   │   ├── repository/     # Data access repositories
│   │   │   └── GenericSimBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/genericsim/backend/
│           └── controller/     # Controller tests
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build

```bash
cd backend
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Flexible Resource and Coefficient System

The simulation now supports a **fully flexible and extensible model** for resources and coefficients. Instead of hardcoding resource types in Java enums, all resources and coefficients are defined in YAML configuration files.

### Key Features

- ✅ **No code changes required** - Add new resources/coefficients by editing YAML
- ✅ **Support for both resources and coefficients** - Manage tangible resources (food, water) and abstract metrics (stability, morale)
- ✅ **Full configurability** - Control min/max values, production/consumption rates, and effects
- ✅ **Export/Import capability** - Save and restore simulation state as JSON
- ✅ **AI-ready** - Dependencies can be calculated by AI layer based on state snapshots

### Documentation

- 📖 **[Resource Configuration Guide](./RESOURCE_CONFIGURATION.md)** - Complete documentation of the configuration format
- 📖 **[Adding New Resource Example](./ADDING_NEW_RESOURCE_EXAMPLE.md)** - Step-by-step guide with examples

### Quick Example

Add a new resource by editing `src/main/resources/application.yml`:

```yaml
simulation:
  resources:
    - id: "metal"
      name: "Metal"
      type: "resource"
      min: 0
      max: 1000
      defaultValue: 0
      description: "Advanced material for tools"
```

That's it! No Java code changes needed. The new resource is immediately available after restart.

## API Endpoints

### Health Check

- **Endpoint:** `GET /api/health`
- **Description:** Returns the health status of the application
- **Response:**
  ```json
  {
    "status": "UP",
    "message": "Generic Simulation Backend is running"
  }
  ```

### Resource Configuration

- **Endpoint:** `GET /api/config`
- **Description:** Get all resource and coefficient configurations
- **Response:** Map of all configurations

- **Endpoint:** `GET /api/config/{id}`
- **Description:** Get a specific resource or coefficient configuration by ID
- **Example:** `GET /api/config/food`

- **Endpoint:** `GET /api/config/resources`
- **Description:** Get all resource configurations

- **Endpoint:** `GET /api/config/coefficients`
- **Description:** Get all coefficient configurations

### H2 Console (Development Only)

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:genericsim`
- **Username:** `sa`
- **Password:** (leave empty)

## Configuration

The application configuration is in `src/main/resources/application.properties`:

- Server port: `8080`
- Database: H2 in-memory
- JPA: Auto DDL update enabled
- H2 Console: Enabled at `/h2-console`

## Next Steps

- Add more simulation-specific entities and endpoints
- Implement simulation execution logic
- Add authentication and authorization
- Configure PostgreSQL for production
- Add more comprehensive tests
