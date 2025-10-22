# Hunter-Gatherer Tribe Simulation

## Overview

The Hunter-Gatherer Tribe Simulation models a basic tribal society with resource management, population dynamics, and policy-based governance. The simulation advances in discrete time steps (ticks) representing days, where tribe members perform actions and resources are consumed.

## Core Entities

### Tribe

Represents a single hunter-gatherer tribe community.

**Core Attributes:**
- `id` - Unique identifier
- `name` - Tribe name
- `description` - Tribe description
- `currentTick` - Current simulation time (day counter)
- `resources` - Available food and water in communal storage
- `policy` - Governing rules and incentives
- `members` - List of all tribe members

**Extended Attributes (Family Features):**
- `families` - List of family units within the tribe
- `tools` - Tribe-owned tools inventory
- `decayStats` - Historical decay tracking and statistics
- `sharingEvents` - Log of resource sharing events
- `skillsLeaderboard` - Top skilled members per category

### Person

Represents an individual member of the tribe.

**Core Attributes:**
- `id` - Unique identifier
- `name` - Person's name
- `role` - Current role (HUNTER, GATHERER, CHILD, ELDER)
- `age` - Age in years
- `health` - Health points (0-100)

**Extended Attributes (Family Features):**
- `familyId` - ID of the family they belong to
- `personalStorage` - Individual inventory (max: 10 food, 10 water, 2 tools)
- `skills` - Skill levels object (hunting, gathering, toolCrafting, teaching, medicine)
- `skillExperience` - Accumulated experience points per skill
- `activitiesLog` - Recent activities for skill progression tracking

**Roles:**
- **HUNTER** - Focuses on gathering food (hunting animals)
  - Benefits from hunting skill progression
  - Can use hunting spears for efficiency boost
  - Gathers 10-20 base food units per tick
  
- **GATHERER** - Collects food and water from the environment
  - Benefits from gathering skill progression
  - Can use gathering baskets for efficiency boost
  - Gathers 5-10 food and 8-16 water units per tick
  
- **CHILD** - Under 16 years old, doesn't contribute to resource gathering
  - Learns skills 50% faster (neuroplasticity)
  - Observes adult activities and gains skill experience passively
  - At age 16, becomes Hunter or Gatherer based on highest trained skill
  
- **ELDER** - Over 60 years old, provides wisdom but reduced productivity
  - Automatically gains +2 teaching skill per year
  - 20% slower at learning new skills
  - Can teach others, accelerating their skill development
  - Often specializes in medicine and tool crafting

### Resources

Represents the tribe's available resources.

**Attributes:**
- `food` - Available food units
- `water` - Available water units

### Policy

Defines the tribe's governing rules including taxation, incentives, and sharing regulations.

**Core Attributes:**
- `name` - Policy name
- `description` - Policy description
- `foodTaxRate` - Percentage of gathered food taken as tax (0-100)
- `waterTaxRate` - Percentage of gathered water taken as tax (0-100)
- `huntingIncentive` - Bonus food units for hunters
- `gatheringIncentive` - Bonus resource units for gatherers

**Extended Attributes (Sharing Policies):**
- `autoSharingEnabled` - Toggle automatic family surplus sharing (boolean)
- `emergencyThreshold` - Health level triggering emergency sharing (default: 20)
- `generosityBonus` - Reputation gain from voluntary sharing (default: 5)
- `hoardingPenalty` - Reputation loss for refusing emergency sharing (default: -15)
- `surplusTaxRate` - Percentage of family surplus contributed to tribe (default: 20%)
- `familyStorageCapacity` - Maximum storage per family (default: 100 food, 100 water)
- `personalStorageCapacity` - Maximum personal inventory (default: 10 food, 10 water)

## Simulation Logic

### Daily Tick Process

Each simulation tick represents one day and follows this enhanced sequence:

1. **Resource Gathering (with Skills)**
   - Hunters collect food: (10-20 base units + hunting incentive) * (1 + huntingSkill/100)
   - Gatherers collect food: (5-10 base units + gathering incentive) * (1 + gatheringSkill/200)
   - Gatherers collect water: (8-16 base units + gathering incentive) * (1 + gatheringSkill/200)
   - Family coordination bonus: +10% when 2+ family members gather together
   - Tool bonus: +20% efficiency per relevant tool in use
   - Only members with health > 30 can effectively gather

2. **Tax Collection**
   - Food tax applied to gathered food deposited to tribe storage
   - Water tax applied to gathered water deposited to tribe storage
   - Family surplus tax applied if family storage exceeds safety margin (30 units)
   - Taxes reduce available resources but fund communal storage

3. **Resource Distribution**
   - Gathered resources deposited to personal storage first (up to 10 capacity)
   - Overflow deposited to family storage (up to 100 capacity each)
   - Further overflow goes to tribe communal storage
   - Family members can access family storage before tribe storage

4. **Storage Decay Processing**
   - Food decay: 2% per tick (1% with preservation tools)
   - Water evaporation: 1% per tick (0.25% with storage containers)
   - Tool degradation: 5% durability loss per use
   - Decay applied to all storage tiers (tribe, family, personal)

5. **Resource Consumption**
   - Each person consumes 3 food units per day
   - Each person consumes 4 water units per day
   - Consumption order: Personal → Family → Tribe storage
   - Skills training consumes additional resources (1 food per 5 ticks of training)

6. **Sharing Evaluation**
   - Check for automatic sharing triggers (tribe storage < 20)
   - Check for emergency sharing needs (any member health < 20)
   - Process family contributions and update reputation
   - Log sharing events for history

7. **Health Updates**
   - Low resources (< 10 food or water): Health decreases by 10
   - Adequate resources: Health increases by 5 (up to 100)
   - Family cohesion bonus: +2 health per tick for family members
   - Medicine skill bonus: Additional +1 health per 10 medicine skill in tribe
   - Prevention: Medicine skill 40+ can prevent death from low health once per person

8. **Skill Development**
   - Passive skill gains for performed activities (+1 per 10 ticks of practice)
   - Active training sessions with teachers (accelerated learning)
   - Skill decay for unused skills (-1 per 100 ticks of non-use)
   - Update skill experience tracking

9. **Aging**
   - Every 365 ticks (1 year), all members age by 1 year
   - Role transitions:
     - Age 16: Children become Hunters or Gatherers based on trained skills
     - Age 60: Adults become Elders, gain teaching skill boost
   - Elders automatically gain +2 teaching skill per year after 60

10. **Death and Family Management**
    - Members with health = 0 are removed from the tribe
    - Orphaned children redistributed to families with capacity
    - Families with no adults are dissolved
    - Inheritance: Deceased member's personal storage distributed to family
    - Update family reputation based on care for members

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

Returns: Current state of the specified tribe, including families and extended attributes.

#### Process a Tick
```
POST /api/tribes/{id}/tick
```

Advances the simulation by one day and returns the updated tribe state. Processes:
- Resource gathering with skill modifiers
- Decay calculations for storage
- Sharing mechanics
- Skill development
- Family dynamics updates

### Family Management Endpoints

#### List Families
```
GET /api/tribes/{tribeId}/families
```

Returns all families within the specified tribe.

#### Get Family Details
```
GET /api/tribes/{tribeId}/families/{familyId}
```

Returns detailed family information including members, storage, and reputation.

#### Create Family
```
POST /api/tribes/{tribeId}/families
Content-Type: application/json

{
  "name": "The Eagle Clan",
  "headOfFamily": 3,
  "initialMembers": [3, 7, 9]
}
```

Creates a new family unit within the tribe.

### Storage Management Endpoints

#### Get Tribe Storage
```
GET /api/tribes/{tribeId}/storage
```

Returns communal tribe storage contents and decay statistics.

#### Get Family Storage
```
GET /api/tribes/{tribeId}/families/{familyId}/storage
```

Returns family-specific storage contents.

#### Get Personal Storage
```
GET /api/tribes/{tribeId}/members/{personId}/storage
```

Returns individual member's personal inventory.

#### Deposit Resources
```
PUT /api/tribes/{tribeId}/storage/deposit
Content-Type: application/json

{
  "personId": 5,
  "targetType": "FAMILY",
  "targetId": 2,
  "food": 15,
  "water": 10
}
```

Deposits resources from personal inventory to family or tribe storage.

#### Withdraw Resources
```
PUT /api/tribes/{tribeId}/storage/withdraw
Content-Type: application/json

{
  "personId": 5,
  "sourceType": "FAMILY",
  "sourceId": 2,
  "food": 10,
  "water": 8
}
```

Withdraws resources from family or tribe storage to personal inventory.

### Sharing Endpoints

#### Manual Share Resources
```
POST /api/tribes/{tribeId}/share
Content-Type: application/json

{
  "fromPersonId": 3,
  "toFamilyId": 4,
  "food": 20,
  "water": 15,
  "reason": "GENEROSITY"
}
```

Manually share resources with another family or member.

#### Get Sharing History
```
GET /api/tribes/{tribeId}/sharing-events?limit=50&offset=0
```

Returns log of sharing events with reputation impacts.

#### Update Sharing Policy
```
PUT /api/tribes/{tribeId}/policy/sharing
Content-Type: application/json

{
  "autoSharingEnabled": true,
  "emergencyThreshold": 20,
  "surplusTaxRate": 25
}
```

Configures tribe-wide sharing policies.

### Skills Endpoints

#### Get Member Skills
```
GET /api/tribes/{tribeId}/members/{personId}/skills
```

Returns all skill levels and experience for a member.

#### Train Skill
```
PUT /api/tribes/{tribeId}/members/{personId}/skills/train
Content-Type: application/json

{
  "skill": "hunting",
  "duration": 10,
  "teacherId": 3
}
```

Conducts training session to improve a specific skill.

#### Skills Leaderboard
```
GET /api/tribes/{tribeId}/skills/leaderboard?skill=hunting&limit=10
```

Returns top members for a specific skill category.

### Tools & Decay Endpoints

#### Get Decay Statistics
```
GET /api/tribes/{tribeId}/storage/decay-stats
```

Returns comprehensive decay statistics and recommendations.

#### List Tools
```
GET /api/tribes/{tribeId}/tools
```

Returns all tools in tribe and family inventories with durability.

#### Craft Tool
```
POST /api/tribes/{tribeId}/tools/craft
Content-Type: application/json

{
  "crafterId": 3,
  "toolType": "HUNTING_SPEAR",
  "materials": {
    "wood": 2,
    "stone": 1
  }
}
```

Crafts a new tool using available materials and crafter's skill.

#### Repair Tool
```
POST /api/tribes/{tribeId}/tools/{toolId}/repair
Content-Type: application/json

{
  "repairerId": 3
}
```

Repairs a damaged tool to restore durability.

## Automated Daily Ticks

A scheduled job runs every day at midnight (00:00:00) to automatically process a tick for all tribes in the system. This ensures continuous simulation progression even without manual API calls.

The scheduler is implemented in `TribeScheduler.java` and can be configured via Spring's `@Scheduled` annotation.

## Initial Tribe Configuration

When a new tribe is created, it starts with:

**Resources:**
- Tribe Storage: 100 food, 100 water
- No tools initially

**Policy (Default):**
- Food Tax Rate: 10%
- Water Tax Rate: 10%
- Hunting Incentive: 5 units
- Gathering Incentive: 5 units
- Auto-Sharing: Enabled
- Emergency Threshold: 20 health
- Surplus Tax Rate: 20%

**Members:**
- 2 Hunters (age 25, 28)
- 2 Gatherers (age 24, 26)
- 1 Child (age 8)
- 1 Elder (age 65, health 80)

**Families:**
- 2 initial families automatically created
- Family 1: Both hunters + child (3 members)
- Family 2: Both gatherers + elder (3 members)
- Each family starts with 20 food, 15 water in family storage
- Initial reputation: 50 for all families

**Skills (Initial):**
- Hunters: Hunting 30, Gathering 10, Other 0
- Gatherers: Gathering 25, Hunting 5, Other 0  
- Child: All skills 0 (rapid learning phase)
- Elder: Teaching 50, Medicine 30, Hunting 60 (experienced)

## Example Simulation Flow

### Day 0 (Initial State)
- Population: 6 members in 2 families
- Tribe Storage: 100 food, 100 water
- Family 1 Storage: 20 food, 15 water (Hunter family)
- Family 2 Storage: 20 food, 15 water (Gatherer family)
- No tools yet
- Initial skills: Hunters (30 hunting), Gatherers (25 gathering), Elder (50 teaching, 30 medicine)

### Day 1 (After First Tick)
**Gathering Phase:**
- Hunter Alpha (skill 30): Gathers 19 food (base 15 * 1.30 = 19.5)
- Hunter Beta (skill 30): Gathers 20 food
- Gatherer Alpha (skill 25): Gathers 7 food, 11 water
- Gatherer Beta (skill 25): Gathers 6 food, 10 water
- Total gathered: 52 food, 21 water

**Tax & Distribution:**
- Tribe tax (10%): 5 food, 2 water → Tribe storage
- Remaining distributed: Personal (10 each) → Family overflow → Tribe
- Family 1 gains: 24 food, 0 water
- Family 2 gains: 8 food, 19 water

**Decay:**
- Tribe food decay: 2.0 units (100 * 0.02)
- Family food decay: 0.4 units each (20 * 0.02)
- Water decay: Half of food decay rates

**Consumption:**
- 18 food consumed (6 members * 3)
- 24 water consumed (6 members * 4)
- Drawn from personal → family → tribe

**Skill Updates:**
- Hunters gain +0.1 hunting skill (practice)
- Gatherers gain +0.1 gathering skill
- Child observes and gains +0.05 in observed skills

**Health & Family:**
- All members: +5 health (adequate resources)
- Family members: +2 health (cohesion bonus)
- Medicine bonus: +1 health (elder's 30 medicine skill)
- Final health: All at maximum (100) except elder (88)

**Net State After Day 1:**
- Tribe Storage: 129 food, 95 water
- Family 1: 42 food, 13 water
- Family 2: 26 food, 29 water
- Skills: Slight improvements across the board

### Week 1 (After 7 Ticks)
- Hunter Beta reaches hunting skill 35
- Tribe storage: 215 food, 140 water
- Family 1 reputation: 52 (small generosity bonus)
- One basic hunting spear crafted by elder (teaching + crafting)
- Total food decayed: ~15 units
- All members healthy (90-100 health)

### Month 1 (After 30 Ticks)
**Milestones:**
- Hunter Beta now has hunting skill 45 (+15 from practice)
- Child (age 8) has learned basic gathering skill 15 (from observation)
- Elder crafted 2 storage containers → reducing decay
- Family 1 reputation increased to 60 (consistent surplus sharing)
- Total resources: 450 food, 380 water across all storages
- Food decay reduced from 2% to 1% (preservation tools active)

**Skills Progression:**
- Hunter Alpha: Hunting 42, Teaching 5
- Hunter Beta: Hunting 45, Tool Crafting 10
- Gatherer Alpha: Gathering 35, Medicine 5
- Gatherer Beta: Gathering 33
- Child: Gathering 15, Hunting 8 (observational learning)
- Elder: Teaching 52, Medicine 32, Tool Crafting 25

**Tool Inventory:**
- 1 Hunting Spear (65% durability)
- 2 Storage Containers (95% durability each)
- Resources consumed in crafting: 15 food, 5 water

### Year 1 (After 365 Ticks)
**Major Changes:**
- Child turns 9 years old
- All adults have aged by 1 year
- Elder is now 66, teaching skill increased to 60
- Multiple skill specializations emerged:
  - Hunter Beta: Primary tool crafter (skill 55)
  - Gatherer Alpha: Developed medicine skill to 35
  - Hunter Alpha: Advanced hunter (skill 75) and mentor (teaching 25)
  
**Family Dynamics:**
- Family 1 reputation: 78 (consistent generosity)
- Family 2 reputation: 65 (stable contributions)
- No emergency sharing events (good resource management)
- 5 automatic sharing events (routine surplus contributions)

**Resource State:**
- Tribe storage: 380 food, 290 water
- Family 1: 95 food, 78 water, 5 tools
- Family 2: 88 food, 85 water, 3 tools
- Total decay over year: ~450 food, ~180 water

**Tool Ecosystem:**
- 3 Hunting Spears (various durability 45-80%)
- 4 Storage Containers (80-95% durability)
- 2 Gathering Baskets (70% durability)
- 12 tools repaired during the year
- 3 tools broken and removed

### Long-term Dynamics (Multi-Year)
- Resources fluctuate based on population, skills, and policy
- Health degrades if resources become scarce
- Family reputation affects tribe cohesion and efficiency
- Skills create specialization and interdependence
- Decay forces active management and tool maintenance
- Population may decline if resources are mismanaged
- Aging changes workforce composition over time
- Children learn from skilled adults, creating generational knowledge transfer
- Tool technology gradually improves with higher skill levels
- Family alliances form based on reputation and sharing patterns

## Family-Based Features

The simulation includes advanced family-based mechanics that add depth to social dynamics, resource management, and skill development within the tribe.

### Families

Represents family units within the tribe, providing structure for reproduction, resource pooling, and social cohesion.

**Attributes:**
- `id` - Unique family identifier
- `name` - Family name (e.g., "The Bear Clan", "River Family")
- `members` - List of Person IDs belonging to the family
- `headOfFamily` - Person ID of the family leader (eldest adult)
- `familyStorage` - Private family storage pool (see Storage section)
- `reputation` - Family standing within the tribe (0-100)

**Family Formation Rules:**
- New families form when two adults (age 18+) form a partnership
- Children born to family members automatically join that family
- Orphaned children (lost both parents) are adopted by families with capacity
- Families with no adult members are dissolved, and children are redistributed
- Maximum family size: 12 members (realistic historical limit)

**Family Benefits:**
- Shared family storage provides security during hard times
- Higher reputation grants bonuses to skill learning rates
- Family members receive health bonuses from social cohesion (+2 health per tick)
- Coordinated resource gathering (10% efficiency bonus when 2+ family members work together)

**API Endpoints:**
```
GET /api/tribes/{tribeId}/families
GET /api/tribes/{tribeId}/families/{familyId}
POST /api/tribes/{tribeId}/families
```

**Example Response:**
```json
{
  "id": 1,
  "name": "The Bear Clan",
  "members": [1, 2, 5, 8],
  "headOfFamily": 1,
  "familyStorage": {
    "food": 30,
    "water": 25,
    "tools": 3
  },
  "reputation": 75
}
```

### Storage

Implements a multi-tiered storage system allowing tribes and families to preserve resources and tools.

**Storage Types:**

1. **Tribe Storage (Communal)**
   - Shared by all tribe members
   - Subject to policy taxation
   - Unlimited capacity
   - Accessible by all members for consumption

2. **Family Storage (Private)**
   - Exclusive to family members
   - Protected from tribal taxation
   - Capacity: 100 food, 100 water, 20 tools per family
   - Members can freely deposit/withdraw

3. **Personal Storage**
   - Individual member inventory
   - Very limited capacity (10 food, 10 water, 2 tools)
   - Used for immediate needs and trading

**Storage Rules:**
- Resources decay over time (see Decay section)
- Storage priority: Personal → Family → Tribe (consumption order)
- Members automatically consume from personal first, then family, then tribe
- Deposits follow reverse priority: Tribe → Family → Personal (overflow)
- Tools don't stack; each counts as one slot

**Storage Attributes:**
- `food` - Stored food units
- `water` - Stored water units  
- `tools` - Count of tools (improves gathering efficiency)
- `lastUpdated` - Timestamp for decay calculations

**API Endpoints:**
```
GET /api/tribes/{tribeId}/storage
GET /api/tribes/{tribeId}/families/{familyId}/storage
GET /api/tribes/{tribeId}/members/{personId}/storage
PUT /api/tribes/{tribeId}/storage/deposit
PUT /api/tribes/{tribeId}/storage/withdraw
```

**Example - Depositing to Family Storage:**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/families/2/storage/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "personId": 5,
    "food": 15,
    "water": 10
  }'
```

**Response:**
```json
{
  "success": true,
  "familyStorage": {
    "food": 45,
    "water": 35,
    "tools": 3
  },
  "personalStorage": {
    "food": 0,
    "water": 0,
    "tools": 1
  }
}
```

### Sharing

Enables resource distribution between tribe members, families, and the communal pool based on configurable rules.

**Sharing Mechanics:**

1. **Automatic Sharing**
   - Triggered when tribe resources fall below threshold (< 20 food or water)
   - Families with surplus automatically contribute to tribe storage
   - Contribution rate: 20% of family surplus above safety margin (30 units)
   - Elders can override automatic sharing for their family

2. **Manual Sharing**
   - Members can gift resources to other members or families
   - Builds reputation for generous families (+5 reputation per significant gift)
   - Reduces reputation for hoarding during tribe scarcity (-10 reputation)

3. **Emergency Sharing**
   - Activated when any member's health drops below 20
   - Families with surplus required to share (policy-enforced)
   - Non-compliance results in reputation penalties (-15 reputation)

**Sharing Policy Attributes:**
- `autoSharingEnabled` - Toggle automatic sharing (default: true)
- `emergencyThreshold` - Health level that triggers emergency sharing (default: 20)
- `generosityBonus` - Reputation gain from voluntary sharing (default: 5)
- `hoardingPenalty` - Reputation loss for refusing emergency sharing (default: -15)
- `surplusTaxRate` - Percentage of family surplus contributed to tribe (default: 20%)

**Sharing Events Log:**
```json
{
  "tick": 156,
  "event": "EMERGENCY_SHARING",
  "fromFamily": 2,
  "toMember": 7,
  "resources": {
    "food": 10,
    "water": 8
  },
  "reputationChange": 0
}
```

**API Endpoints:**
```
POST /api/tribes/{tribeId}/share
GET /api/tribes/{tribeId}/sharing-events
PUT /api/tribes/{tribeId}/policy/sharing
```

**Example - Manual Gift:**
```bash
curl -X POST http://localhost:8080/api/tribes/1/share \
  -H "Content-Type: application/json" \
  -d '{
    "fromPersonId": 3,
    "toFamilyId": 4,
    "food": 20,
    "water": 15
  }'
```

### Skills

Represents learned abilities that improve individual and family efficiency in various tasks.

**Core Skills:**

1. **Hunting Proficiency** (0-100)
   - Increases food gathered by hunters
   - Formula: base_food * (1 + skill/100)
   - Example: Skill 50 → 50% more food from hunting

2. **Gathering Expertise** (0-100)
   - Improves food and water collection for gatherers
   - Reduces resource consumption risk (spoilage)
   - Formula: base_resources * (1 + skill/200) [half effect of hunting]

3. **Tool Crafting** (0-100)
   - Ability to create and repair tools
   - Higher skill = better quality tools (last longer)
   - Skill 60+ required to craft advanced tools

4. **Teaching** (0-100)
   - Accelerates skill learning for family members
   - Elders gain teaching skill naturally (+2 per year after age 60)
   - Formula: student_learning_rate * (1 + teacher_skill/100)

5. **Medicine** (0-100)
   - Improves health recovery rate
   - Prevents death from low health (skill 40+)
   - Formula: health_recovery = base + (skill/10)

**Skill Development:**
- Skills increase through practice: +1 per 10 ticks spent performing relevant task
- Children learn 50% faster than adults (neuroplasticity)
- Family teaching bonus: +30% learning rate when high-skill family member teaches
- Skill decay: -1 per 100 ticks of non-use (use it or lose it)
- Aging affects learning: -20% learning rate for elders (60+)

**Person Skill Attributes:**
```json
{
  "id": 3,
  "name": "Skilled Hunter",
  "age": 32,
  "role": "HUNTER",
  "skills": {
    "hunting": 75,
    "gathering": 30,
    "toolCrafting": 45,
    "teaching": 20,
    "medicine": 10
  },
  "skillExperience": {
    "hunting": 750,
    "gathering": 300
  }
}
```

**Skill Impact on Simulation:**
- Daily resource gathering modified by relevant skill levels
- Tool durability extended by crafter's skill
- Health recovery accelerated by tribe's total medicine skill pool
- Children's transition to adult roles influenced by learned skills

**API Endpoints:**
```
GET /api/tribes/{tribeId}/members/{personId}/skills
PUT /api/tribes/{tribeId}/members/{personId}/skills/train
GET /api/tribes/{tribeId}/skills/leaderboard
```

**Example - Training Session:**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/members/5/skills/train \
  -H "Content-Type: application/json" \
  -d '{
    "skill": "hunting",
    "duration": 10,
    "teacherId": 3
  }'
```

### Decay

Implements realistic degradation of resources and tools over time, requiring active management and preservation strategies.

**Decay Types:**

1. **Food Decay**
   - Fresh food spoils over time
   - Decay rate: 2% per tick (day) for food in storage
   - Temperature affects decay (not yet implemented: seasonal variation)
   - Preservation: Tools (smoking racks, drying racks) reduce decay by 50%

2. **Water Evaporation**
   - Stored water evaporates in open containers
   - Evaporation rate: 1% per tick
   - Storage containers (clay pots - tools) reduce loss by 75%

3. **Tool Degradation**
   - Tools wear out with use
   - Degradation: 5% durability loss per use
   - Quality affects degradation rate (high skill crafted tools last longer)
   - Broken tools (0% durability) are removed from inventory

**Decay Mechanics:**

**Food Decay Formula:**
```
decay_amount = stored_food * base_decay_rate * (1 - preservation_modifier)
where:
  base_decay_rate = 0.02 (2% per day)
  preservation_modifier = 0.5 if preservation tools present, else 0
```

**Tool Durability:**
- New tools: 100% durability
- Each use: durability -= 5 * (1 - quality_bonus)
- Quality bonus: crafter_skill / 200 (max 50% reduction at skill 100)
- Repairs restore 40% durability (requires Tool Crafting skill 30+)

**Decay Mitigation Strategies:**
1. **Active Preservation**
   - Craft preservation tools (smoking racks, storage containers)
   - Consume oldest resources first (FIFO queue)
   - Maintain optimal storage conditions

2. **Resource Rotation**
   - Regularly cycle through stored resources
   - Transfer surplus to families before significant decay
   - Balance fresh gathering vs. storage reliance

3. **Tool Maintenance**
   - Designate tool crafters for regular repairs
   - Craft higher quality tools (last longer)
   - Maintain tool reserves for replacements

**Decay Impact on Simulation:**
- Stored resources gradually diminish even without consumption
- Forces active resource management strategies
- Makes fresh gathering more valuable than hoarding
- Creates need for tool crafters in every tribe
- Encourages trading of preservation tools between families

**Storage Decay Tracking:**
```json
{
  "storage": {
    "food": 100,
    "water": 80,
    "tools": [
      {
        "type": "HUNTING_SPEAR",
        "durability": 75,
        "quality": 60,
        "craftedBy": 3
      },
      {
        "type": "STORAGE_CONTAINER",
        "durability": 90,
        "quality": 80,
        "effect": "PRESERVATION"
      }
    ]
  },
  "decayLog": [
    {
      "tick": 145,
      "foodDecayed": 2.0,
      "waterEvaporated": 0.8,
      "preservationActive": true
    }
  ]
}
```

**API Endpoints:**
```
GET /api/tribes/{tribeId}/storage/decay-stats
POST /api/tribes/{tribeId}/tools/{toolId}/repair
GET /api/tribes/{tribeId}/tools
POST /api/tribes/{tribeId}/tools/craft
```

**Example - Viewing Decay Statistics:**
```bash
curl http://localhost:8080/api/tribes/1/storage/decay-stats
```

**Response:**
```json
{
  "currentTick": 200,
  "totalFoodDecayed": 145.5,
  "totalWaterEvaporated": 62.3,
  "toolsBroken": 7,
  "preservationEfficiency": 0.65,
  "recommendations": [
    "Craft more storage containers to reduce water evaporation",
    "Designate a dedicated tool crafter for repairs",
    "Consider consuming oldest stored resources first"
  ]
}
```

### Integrated Feature Example

**Scenario: Winter Preparation (Day 100-150)**

The Bear Clan family (4 members) prepares for harsh winter:

**Day 100:**
- Elder (teaching skill 70) trains young hunter in Hunting Proficiency
- Family has: 45 food, 30 water, 2 tools in family storage
- Tribe storage: 120 food, 100 water

**Day 110:**
- Young hunter's hunting skill increased from 20 to 35 (teaching bonus)
- Gathers 18 food (base 15 + 20% skill bonus), deposits to family storage
- Family storage: 61 food (45 + 18 - 2 decay)

**Day 120:**
- Automatic sharing triggered: tribe storage < 20 food
- Bear Clan contributes 8 food (20% of surplus above 30 safety margin)
- Reputation increases from 70 to 75 (+5 for generosity)

**Day 130:**
- Tool crafter (tool crafting skill 55) creates storage container
- Preservation modifier active: food decay reduced from 2% to 1%
- Family storage protected from excessive decay

**Day 140:**
- Emergency sharing activated: tribe member health drops to 15
- Bear Clan shares 10 food, 8 water with struggling family
- Reputation maintained (no penalty), receives tribe-wide recognition

**Day 150:**
- Family survives winter with 52 food, 28 water remaining
- Skills improved: hunting +15, tool crafting +10, teaching +5
- Tools: 1 hunting spear (75% durability), 1 storage container (90% durability)
- Overall family reputation: 82 (started at 70)

This example demonstrates how families, storage, sharing, skills, and decay interact to create meaningful strategic decisions in the simulation.

## Future Enhancements

Potential additions to the simulation:
- Birth/reproduction mechanics integrated with families
- Random events (droughts, abundant seasons) affecting decay rates
- Inter-tribe trade or conflict over resources
- Technology/tool development trees with skill requirements
- Territory/land management with family claims
- Disease and epidemic modeling within families
- Migration patterns driven by resource scarcity
- Seasonal variations affecting gathering and decay
- Advanced tool types with specialized benefits
- Family alliances and political dynamics
