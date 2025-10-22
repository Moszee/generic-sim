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
