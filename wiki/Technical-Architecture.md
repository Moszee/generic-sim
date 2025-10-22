# Technical Architecture

## System Overview

The Generic Simulation system follows a three-tier architecture:

1. **Frontend Layer** - React-based web UI
2. **Backend Layer** - Spring Boot REST API
3. **Data Layer** - H2 in-memory database (JPA/Hibernate)

## Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.1** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **H2 Database** - In-memory database
- **Maven** - Build tool
- **JUnit 5** - Testing framework

### Frontend
- **React** - UI framework
- **JavaScript/ES6** - Programming language
- **npm** - Package manager

## Backend Architecture

### Layer Structure

```
┌─────────────────────────────────────┐
│        REST Controllers             │
│  (HTTP Request/Response Handling)   │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│         Service Layer               │
│    (Business Logic & Simulation)    │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│       Repository Layer              │
│    (Data Access via Spring Data)    │
└─────────────────────────────────────┘
                ↓
┌─────────────────────────────────────┐
│          H2 Database                │
│    (In-Memory Persistence)          │
└─────────────────────────────────────┘
```

### Component Details

#### Models (Entities)

**Tribe Entity:**
- Primary entity representing a tribal community
- One-to-One relationships with Resources and Policy
- One-to-Many relationship with Person (members)
- Tracks simulation time via `currentTick`

**Person Entity:**
- Represents individual tribe members
- Enum-based role system (HUNTER, GATHERER, CHILD, ELDER)
- Many-to-One relationship with Tribe
- Health and age tracking

**Resources Entity:**
- Stores food and water quantities
- One-to-One relationship with Tribe
- Simple integer-based resource model

**Policy Entity:**
- Defines governance rules
- Tax rates and incentive bonuses
- One-to-One relationship with Tribe

#### Services

**TribeService:**
- Core business logic for tribe simulation
- Implements tick processing algorithm:
  1. Resource gathering by role
  2. Tax application
  3. Resource consumption
  4. Health updates
  5. Aging mechanics
  6. Death processing
- DTO conversion for API responses
- Transaction management with `@Transactional`

**SimulationService:**
- Generic simulation operations (future expansion)
- Currently manages Simulation entities (metadata)

#### Controllers

**TribeController:**
- REST endpoints for tribe management
- CRUD operations for tribes
- Tick processing endpoint
- JSON request/response handling

**HealthController:**
- System health check endpoint
- Used for monitoring and deployment verification

#### Repositories

**TribeRepository:**
- Spring Data JPA repository
- Automatic CRUD operations
- Query method generation

**PersonRepository:**
- Person entity data access
- Used for member management

**SimulationRepository:**
- Simulation metadata storage

#### Scheduler

**TribeScheduler:**
- Scheduled task for automated tick processing
- Runs daily at midnight (00:00:00)
- Processes all tribes in the system
- Error handling for individual tribe failures

### Database Schema

```
tribes
├── id (PK)
├── name
├── description
├── current_tick
├── resources_id (FK → resources)
└── policy_id (FK → policies)

persons
├── id (PK)
├── name
├── role (ENUM)
├── age
├── health
└── tribe_id (FK → tribes)

resources
├── id (PK)
├── food
└── water

policies
├── id (PK)
├── name
├── description
├── food_tax_rate
├── water_tax_rate
├── hunting_incentive
└── gathering_incentive

simulations
├── id (PK)
├── name
├── description
└── status
```

## Simulation Algorithm

### Tick Processing Flow

```
START TICK
    ↓
INCREMENT TICK COUNTER
    ↓
GATHER RESOURCES
  ├── For each HUNTER: food += (10-20) + hunting_incentive
  ├── For each GATHERER: 
  │   ├── food += (5-10) + gathering_incentive
  │   └── water += (8-16) + gathering_incentive
  └── (Only if health > 30)
    ↓
APPLY TAXES
  ├── food_tax = (food_gathered * tax_rate) / 100
  └── water_tax = (water_gathered * tax_rate) / 100
    ↓
CONSUME RESOURCES
  ├── food -= members_count * 3
  └── water -= members_count * 4
    ↓
UPDATE HEALTH
  ├── IF resources < 10: health -= 10
  └── ELSE IF health < 100: health += 5
    ↓
AGE MEMBERS (every 365 ticks)
  ├── age += 1
  └── Update role based on age
    ↓
REMOVE DEAD (health = 0)
    ↓
SAVE STATE
    ↓
END TICK
```

## API Design

### RESTful Principles
- Resource-based URLs (`/api/tribes/{id}`)
- HTTP methods for operations (GET, POST)
- JSON content type
- Stateless requests

### Response Format
All responses return TribeStateDTO with complete tribe state:
- Tribe metadata (id, name, description, tick)
- Resource levels (food, water)
- Policy configuration
- Member list with details

## Configuration

### Application Properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:genericsim
spring.jpa.hibernate.ddl-auto=update

# H2 Console
spring.h2.console.enabled=true
```

### Scheduling Configuration
- Uses Spring's `@EnableScheduling` annotation
- Cron expression: `0 0 0 * * *` (midnight daily)
- Timezone: Server default

## Testing Strategy

### Unit Tests
- Service layer tests with `@SpringBootTest`
- Controller tests with `MockMvc`
- Transaction rollback for test isolation
- H2 in-memory database for tests

### Test Coverage
- TribeService: Creation, tick processing, state retrieval
- TribeController: All API endpoints
- Health checks and error cases

## Scalability Considerations

### Current Limitations
- In-memory database (data lost on restart)
- Single-instance scheduling (no distributed locks)
- Synchronous tick processing
- No caching layer

### Future Improvements
- Persistent database (PostgreSQL, MySQL)
- Distributed scheduling (Quartz, ShedLock)
- Asynchronous processing with message queues
- Redis caching for tribe states
- Horizontal scaling with load balancer

## Security

### Current State
- No authentication/authorization
- Public API endpoints
- Development-only setup

### Production Requirements
- Spring Security integration
- JWT or OAuth2 authentication
- Role-based access control
- API rate limiting
- Input validation and sanitization

## Performance

### Optimization Strategies
- Batch processing in scheduler
- Lazy loading for entity relationships
- Database indexing on foreign keys
- DTO pattern to avoid entity exposure
- Transaction boundaries for consistency

### Monitoring
- Spring Boot Actuator endpoints
- Application logging (SLF4J)
- Database query logging (optional)
- Scheduler execution logging
