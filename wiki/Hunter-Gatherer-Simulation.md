# Hunter-Gatherer Tribe Simulation

## Overview

The Hunter-Gatherer Tribe Simulation models a basic tribal society with resource management, population dynamics, and policy-based governance. The simulation advances in discrete time steps (ticks) representing days, where tribe members perform actions and resources are consumed.

## Core Entities

### Tribe

Represents a single hunter-gatherer tribe community.

**Attributes:**
- `id` - Unique identifier
- `name` - Tribe name
- `description` - Tribe description
- `currentTick` - Current simulation time (day counter)
- `resources` - Available food and water
- `policy` - Governing rules and incentives
- `members` - List of all tribe members

### Person

Represents an individual member of the tribe.

**Attributes:**
- `id` - Unique identifier
- `name` - Person's name
- `role` - Current role (HUNTER, GATHERER, CHILD, ELDER)
- `age` - Age in years
- `health` - Health points (0-100)

**Roles:**
- **HUNTER** - Focuses on gathering food (hunting animals)
- **GATHERER** - Collects food and water from the environment
- **CHILD** - Under 16 years old, doesn't contribute to resource gathering
- **ELDER** - Over 60 years old, provides wisdom but reduced productivity

### Resources

Represents the tribe's available resources.

**Attributes:**
- `food` - Available food units
- `water` - Available water units

### Policy

Defines the tribe's governing rules including taxation and incentives.

**Attributes:**
- `name` - Policy name
- `description` - Policy description
- `foodTaxRate` - Percentage of gathered food taken as tax (0-100)
- `waterTaxRate` - Percentage of gathered water taken as tax (0-100)
- `huntingIncentive` - Bonus food units for hunters
- `gatheringIncentive` - Bonus resource units for gatherers

## Simulation Logic

### Daily Tick Process

Each simulation tick represents one day and follows this sequence:

1. **Resource Gathering**
   - Hunters collect food: 10-20 base units + hunting incentive
   - Gatherers collect food: 5-10 base units + gathering incentive
   - Gatherers collect water: 8-16 base units + gathering incentive
   - Only members with health > 30 can effectively gather

2. **Tax Collection**
   - Food tax applied to gathered food
   - Water tax applied to gathered water
   - Taxes reduce available resources

3. **Resource Consumption**
   - Each person consumes 3 food units per day
   - Each person consumes 4 water units per day

4. **Health Updates**
   - Low resources (< 10 food or water): Health decreases by 10
   - Adequate resources: Health increases by 5 (up to 100)

5. **Aging**
   - Every 365 ticks (1 year), all members age by 1 year
   - Role transitions:
     - Age 16: Children become Hunters or Gatherers
     - Age 60: Adults become Elders

6. **Death**
   - Members with health = 0 are removed from the tribe

## REST API Endpoints

### Create a New Tribe
```
POST /api/tribes
Content-Type: application/json

{
  "name": "Northern Tribe",
  "description": "A tribe from the northern region"
}
```

Returns: Tribe state JSON with initial members and resources.

### Get All Tribes
```
GET /api/tribes
```

Returns: Array of all tribe states.

### Get Tribe State
```
GET /api/tribes/{id}
```

Returns: Current state of the specified tribe.

### Process a Tick
```
POST /api/tribes/{id}/tick
```

Advances the simulation by one day and returns the updated tribe state.

## Automated Daily Ticks

A scheduled job runs every day at midnight (00:00:00) to automatically process a tick for all tribes in the system. This ensures continuous simulation progression even without manual API calls.

The scheduler is implemented in `TribeScheduler.java` and can be configured via Spring's `@Scheduled` annotation.

## Initial Tribe Configuration

When a new tribe is created, it starts with:

**Resources:**
- Food: 100 units
- Water: 100 units

**Policy (Default):**
- Food Tax Rate: 10%
- Water Tax Rate: 10%
- Hunting Incentive: 5 units
- Gathering Incentive: 5 units

**Members:**
- 2 Hunters (age 25, 28)
- 2 Gatherers (age 24, 26)
- 1 Child (age 8)
- 1 Elder (age 65, health 80)

## Example Simulation Flow

### Day 0 (Initial State)
- Population: 6 members
- Food: 100 units
- Water: 100 units

### Day 1 (After First Tick)
- Hunters gather: ~30-40 food
- Gatherers gather: ~20-30 food, ~30-40 water
- Tax collected: ~5-7 food, ~3-4 water
- Consumption: 18 food, 24 water
- Net change: Resources increase initially

### Long-term Dynamics
- Resources fluctuate based on population and policy
- Health degrades if resources become scarce
- Population may decline if resources are mismanaged
- Aging changes workforce composition over time

## Future Enhancements

Potential additions to the simulation:
- Birth/reproduction mechanics
- Random events (droughts, abundant seasons)
- Inter-tribe trade or conflict
- Technology/tool development
- Territory/land management
- Disease and epidemic modeling
- Migration patterns
