# Hunter-Gatherer Tribe Simulation

## Overview

The Hunter-Gatherer Tribe Simulation models a basic tribal society with resource management, population dynamics, family structures, and policy-based governance. The simulation advances in discrete time steps (ticks) representing days, where tribe members perform actions, resources are consumed, and families interact.

## Core Entities

### Tribe

Represents a single hunter-gatherer tribe community.

**Core Attributes:**
- `id` - Unique identifier
- `name` - Tribe name
- `description` - Tribe description
- `currentTick` - Current simulation time (day counter)
- `resources` - Available food and water (aggregated from all families)
- `centralStorage` - Tribe-level central storage (policy-based)
- `bondLevel` - Tribe cohesion level (0-100) affecting sharing success
- `policy` - Governing rules and incentives
- `members` - List of all tribe members
- `families` - List of family units within the tribe

### Person

Represents an individual member of the tribe.

**Core Attributes:**
- `id` - Unique identifier
- `name` - Person's name
- `role` - Current role (HUNTER, GATHERER, CHILD, ELDER)
- `age` - Age in years
- `health` - Health points (0-100)
- `familyId` - ID of the family they belong to
- `huntingSkill` - Hunting skill level (0.0-1.0)
- `gatheringSkill` - Gathering skill level (0.0-1.0)

**Roles:**
- **HUNTER** - Focuses on gathering food (hunting animals)
  - Benefits from hunting skill progression
  - Gathers 10-20 base food units per tick, scaled by skill
  
- **GATHERER** - Collects food and water from the environment
  - Benefits from gathering skill progression
  - Gathers 5-10 food and 8-16 water units per tick, scaled by skill
  
- **CHILD** - Under 16 years old, doesn't contribute to resource gathering
  - At age 16, becomes Hunter or Gatherer
  
- **ELDER** - Over 60 years old, provides wisdom but reduced productivity
  - Maintains skills from previous role

### Family

Represents a family unit within the tribe.

**Attributes:**
- `id` - Unique identifier
- `name` - Family name (e.g., "Family A", "Family B")
- `members` - List of Person objects belonging to the family
- `storage` - Family-specific resource storage (food and water)

**Family Formation:**
- Families are automatically created when a tribe is initialized
- Typically 2-3 families are created per tribe
- Members are distributed roughly evenly across families
- Each family starts with 30 food and 30 water in storage

### Resources

Represents available resources.

**Attributes:**
- `food` - Available food units
- `water` - Available water units

### Policy

Defines the tribe's governing rules including taxation, incentives, storage, and sharing regulations.

**Core Attributes:**
- `name` - Policy name
- `description` - Policy description
- `foodTaxRate` - Percentage of gathered food taken as tax (0-100) - *Legacy, not actively used*
- `waterTaxRate` - Percentage of gathered water taken as tax (0-100) - *Legacy, not actively used*
- `huntingIncentive` - Bonus food units for hunters
- `gatheringIncentive` - Bonus resource units for gatherers

**Family & Storage Attributes:**
- `sharingPriority` - Who suffers when sharing fails (ELDER, CHILD, HUNTER, GATHERER, YOUNGEST, RANDOM)
- `enableCentralStorage` - Toggle central storage system (boolean)
- `centralStorageTaxRate` - Percentage of gathered resources contributed to central storage (0-100)
- `storageDecayRate` - Percentage of storage lost per decay interval (0.0-1.0, default 0.1 = 10%)
- `storageDecayInterval` - Number of ticks between decay events (default 20)

## Simulation Logic

### Daily Tick Process

Each simulation tick represents one day and follows this sequence:

1. **Resource Gathering (Skill-Based)**
   - Hunters collect food: (10-20 base units + hunting incentive) * (1 + huntingSkill)
   - Gatherers collect food: (5-10 base units + gathering incentive) * (1 + gatheringSkill)
   - Gatherers collect water: (8-16 base units + gathering incentive) * (1 + gatheringSkill)
   - Only members with health > 30 can effectively gather
   - Resources are deposited to family storage

2. **Central Storage Tax (If Enabled)**
   - If `enableCentralStorage` is true, a percentage of gathered resources is taxed
   - Tax rate: `centralStorageTaxRate` percentage
   - Taxed resources go to tribe's central storage
   - Remaining resources go to family storage

3. **Family Resource Consumption**
   - Each person consumes 3 food units per day
   - Each person consumes 4 water units per day
   - Consumption drawn from family storage
   - If sufficient resources: family members' health increases by +5 (up to 100)
   - If insufficient: family attempts to borrow from other families

4. **Inter-Family Borrowing**
   - When family storage is insufficient, borrowing is attempted
   - Families with surplus are checked (richest first)
   - Borrowing success depends on tribe `bondLevel`:
     - Higher bond level (closer to 100) = higher success chance
     - Lower bond level (closer to 0) = lower success chance
   - Successful sharing: bond level increases by +1
   - Failed sharing: bond level decreases by -2

5. **Central Storage Fallback**
   - If borrowing fails and central storage is enabled
   - Family can draw from central storage
   - Resources transferred to family storage

6. **Sharing Failure Consequences**
   - If all sharing mechanisms fail, a family member suffers
   - Member selection based on `sharingPriority` policy:
     - ELDER: Oldest member or elders suffer first
     - CHILD: Youngest member or children suffer first
     - HUNTER: Hunters suffer first
     - GATHERER: Gatherers suffer first
     - YOUNGEST: Youngest member suffers
     - RANDOM: Random member selected
   - Selected member loses 15 health points

7. **Storage Decay (Periodic)**
   - Occurs every `storageDecayInterval` ticks (default: 20 days)
   - All family storages decay by `storageDecayRate` (default: 10%)
   - Central storage also decays if enabled
   - Simulates food spoilage and water evaporation

8. **Skill Development**
   - Successful hunting (food > 15): hunting skill +0.01
   - Successful gathering (food > 7 or water > 10): gathering skill +0.01
   - Skills are capped at 1.0 (maximum proficiency)
   - Skills improve gathering efficiency over time

9. **Aging**
   - Every 365 ticks (1 year), all members age by 1 year
   - Role transitions:
     - Age 16: Children become Hunters or Gatherers
     - Age 60: Adults become Elders
   - When children become adults, they gain initial skills (0.5 in their assigned role)

10. **Death**
    - Members with health = 0 are removed from the tribe
    - Families continue to exist even if members die

## REST API Endpoints

### Core Tribe Endpoints

#### Create a New Tribe
```
POST /api/tribes
Content-Type: application/json

{
  "name": "Northern Tribe",
  "description": "A tribe from the northern region"
}
```

Returns: Tribe state JSON with initial members, resources, and families.

#### Get All Tribes
```
GET /api/tribes
```

Returns: Array of all tribe states.

#### Get Tribe State
```
GET /api/tribes/{id}
```

Returns: Current state of the specified tribe, including:
- Tribe details (name, description, currentTick, bondLevel)
- Resources (tribe total and central storage)
- Policy settings
- All members with their skills and family assignments
- All families with their storage

#### Get Tribe Statistics
```
GET /api/tribes/{id}/statistics
```

Returns: Aggregated statistics about the tribe:
- Population counts
- Role breakdown
- Health statistics
- Resource status

#### Process a Tick
```
POST /api/tribes/{id}/tick
```

Advances the simulation by one day and returns the updated tribe state. Processes:
- Resource gathering with skill modifiers
- Family consumption and sharing
- Storage decay (periodic)
- Skill development
- Aging and role transitions
- Health updates

#### Update Tribe Policy
```
PUT /api/tribes/{id}/policy
Content-Type: application/json

{
  "huntingIncentive": 7,
  "gatheringIncentive": 6,
  "enableCentralStorage": true,
  "centralStorageTaxRate": 15,
  "sharingPriority": "CHILD",
  "storageDecayRate": 0.05,
  "storageDecayInterval": 30
}
```

Updates the tribe's policy settings. Only provided fields are updated.

## Automated Daily Ticks

A scheduled job runs every day at midnight (00:00:00) to automatically process a tick for all tribes in the system. This ensures continuous simulation progression even without manual API calls.

The scheduler is implemented in `TribeScheduler.java` and can be configured via Spring's `@Scheduled` annotation.

## Initial Tribe Configuration

When a new tribe is created, it starts with:

**Resources:**
- Tribe Storage: 100 food, 100 water (aggregated view)
- Central Storage: 0 food, 0 water (if enabled)

**Policy (Default):**
- Food Tax Rate: 10% (legacy, not used)
- Water Tax Rate: 10% (legacy, not used)
- Hunting Incentive: 5 units
- Gathering Incentive: 5 units
- Sharing Priority: ELDER
- Central Storage: Disabled
- Central Storage Tax Rate: 10%
- Storage Decay Rate: 10% (0.1)
- Storage Decay Interval: 20 ticks

**Members:**
- 2 Hunters (age 25, 28)
- 2 Gatherers (age 24, 26)
- 1 Child (age 8)
- 1 Elder (age 65, health 80)

**Families:**
- 2 initial families automatically created
- Family A: 3 members with 30 food, 30 water storage
- Family B: 3 members with 30 food, 30 water storage
- Members distributed evenly across families

**Skills (Initial):**
- Hunters: Hunting 0.6-0.7, Gathering 0.5
- Gatherers: Gathering 0.6-0.65, Hunting 0.5
- Child: All skills 0.5
- Elder: All skills 0.5

**Tribe Attributes:**
- Bond Level: 50 (neutral starting point)
- Current Tick: 0

## Example Simulation Flow

### Day 0 (Initial State)
- Population: 6 members in 2 families
- Tribe Total Resources: 100 food, 100 water (60 in families, 40 reserved)
- Family A Storage: 30 food, 30 water (3 members)
- Family B Storage: 30 food, 30 water (3 members)
- Central Storage: 0 food, 0 water (disabled)
- Bond Level: 50
- Initial skills: Hunters (0.6-0.7 hunting), Gatherers (0.6-0.65 gathering)

### Day 1 (After First Tick)
**Gathering Phase:**
- Hunter Alpha (skill 0.6): Gathers ~24 food (base 15 * 1.6 skill multiplier + 5 incentive)
- Hunter Beta (skill 0.7): Gathers ~27 food
- Gatherer Alpha (skill 0.6): Gathers ~13 food, ~21 water
- Gatherer Beta (skill 0.65): Gathers ~14 food, ~22 water
- Total gathered: ~78 food, ~43 water

**Resource Distribution:**
- Resources deposited to family storage
- Family A (hunters): +51 food, 0 water
- Family B (gatherers): +27 food, +43 water

**Consumption:**
- Family A: -9 food (3 members * 3), -12 water (3 members * 4)
- Family B: -9 food, -12 water
- Family A has sufficient food but low water (18 left)
- Family B has sufficient both (48 food, 61 water)

**Health Updates:**
- All members have adequate resources: +5 health
- All members at 100 health (or elder at 85)

**Skills:**
- Hunters gain +0.01 hunting skill (successful hunts)
- Gatherers gain +0.01 gathering skill
- New skills: Hunters (0.61-0.71), Gatherers (0.61-0.66)

**Final State After Day 1:**
- Family A: 72 food, 18 water
- Family B: 48 food, 61 water
- Tribe Total: 120 food, 79 water
- All members healthy

### Week 1 (After 7 Ticks)
- Hunter skills improved to ~0.67-0.77
- Gatherer skills improved to ~0.67-0.72
- Family A: ~80 food, ~15 water (may need to borrow water)
- Family B: ~65 food, ~90 water
- If Family A needs water: borrowing occurs based on bond level
- Bond level adjusts: 50 → 51-52 (successful sharing) or 48-49 (failed sharing)

### Month 1 (After 30 Ticks)
- Skills continue to improve steadily
- Storage decay occurs once (at day 20) if default policy
- 10% of storage decayed: ~8-10 food and water lost per family
- Families adapt gathering to compensate for decay
- Bond level fluctuates between 45-55 depending on sharing events

### Year 1 (After 365 Ticks)
**Major Changes:**
- Child turns 9 years old (still a child until 16)
- All adults aged by 1 year
- Hunter skills: 0.9-1.0 (approaching maximum)
- Gatherer skills: 0.85-0.95
- Storage decay occurred ~18 times (every 20 days)
- Bond level stabilized around 50-60 based on family dynamics

**Resource State:**
- Total resources fluctuate but remain stable due to skill improvements
- Families maintain adequate storage through skilled gathering
- No deaths due to good resource management

## Key Features Demonstrated

### Family System
- **Family Storage**: Each family manages its own resources independently
- **Member Assignment**: All tribe members belong to a family
- **Consumption**: Families consume resources from their own storage first

### Skills System
- **Skill Progression**: Skills improve with practice (+0.01 per successful gathering)
- **Skill Impact**: Higher skills = more resources gathered
- **Skill Cap**: Skills max out at 1.0 (100% proficiency)

### Sharing Mechanism
- **Inter-Family Borrowing**: Families can borrow from each other when short
- **Bond-Based Success**: Tribe bond level affects sharing success rate
- **Bond Dynamics**: Successful sharing increases bond, failures decrease it
- **Priority Policy**: Determines who suffers when sharing fails

### Central Storage (Optional)
- **Tax Collection**: When enabled, % of gathered resources go to central storage
- **Fallback Resource**: Families can access central storage when depleted
- **Policy Controlled**: Can be enabled/disabled and tax rate adjusted

### Storage Decay
- **Periodic Loss**: Every N ticks (default 20), storage decays by X% (default 10%)
- **All Storages**: Affects both family and central storage
- **Realistic Simulation**: Represents food spoilage and water evaporation

## Future Enhancements

Potential additions to the simulation:
- Birth/reproduction mechanics integrated with families
- Random events (droughts, abundant seasons) affecting resource availability
- Inter-tribe trade or conflict
- More advanced skill system with specializations
- Tool crafting and usage
- Seasonal variations
- Disease modeling
- Family reputation and alliances
