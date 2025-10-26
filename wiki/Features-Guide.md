# Features Guide

This guide provides comprehensive documentation for the key features of the Hunter-Gatherer Tribe Simulation, including detailed UI/UX information, API usage examples, and workflows.

## Table of Contents

1. [Tribe Statistics Display](#tribe-statistics-display)
2. [Manual Tick Execution](#manual-tick-execution)
3. [Tribe Policy Management](#tribe-policy-management)
4. [Feature Summary Table](#feature-summary-table)

---

## Tribe Statistics Display

The Tribe Statistics feature provides a comprehensive view of your tribe's current state, including population, health, resources, and policy information.

### Frontend UI/UX

The Tribe Statistics page (`/tribes` view) displays aggregated statistics in a user-friendly dashboard format.

#### UI Components

1. **Tribe Selector** (Dropdown)
   - Located at the top of the page
   - Lists all available tribes with their current day count
   - Format: `{Tribe Name} (Day {currentTick})`
   - Automatically selects the first tribe on page load
   - Triggers statistics refresh when selection changes

2. **Population Overview** (Card Section)
   - **Total Population**: Count of all living tribe members
   - **Current Day**: Current simulation tick/day number
   - Large, easy-to-read numbers for quick assessment

3. **Role Distribution** (Card Grid)
   - **Hunters**: Number of tribe members with HUNTER role
   - **Gatherers**: Number of tribe members with GATHERER role
   - **Children**: Number of tribe members with CHILD role (under 16 years)
   - **Elders**: Number of tribe members with ELDER role (over 60 years)
   - Color-coded cards for easy visual distinction

4. **Health Statistics** (Card Grid)
   - **Average Health**: Mean health across all members (0-100 scale)
   - **Minimum Health**: Lowest health value among members
   - **Maximum Health**: Highest health value among members
   - **Healthy Members**: Count of members with health ≥ 70
     - Subtitle: "Health ≥ 70" for clarity

5. **Resource Status** (Badge + Card Grid)
   - **Resource Status Badge**: Large, color-coded indicator
     - **ABUNDANT** (Green): 10+ food and water per person
       - Description: "Plentiful resources, tribe is thriving"
     - **ADEQUATE** (Yellow): 5-9 food and water per person
       - Description: "Sufficient resources, tribe is stable"
     - **LOW** (Orange): 3-4 food and water per person
       - Description: "Resources running low, action may be needed"
     - **CRITICAL** (Red): Less than 3 food or water per person
       - Description: "Severe resource shortage, tribe at risk"
   
   - **Total Food**: Current food units in tribe storage
     - Subtitle shows per-person calculation
   
   - **Total Water**: Current water units in tribe storage
     - Subtitle shows per-person calculation

6. **Policy Summary** (Card Grid)
   - **Food Tax Rate**: Percentage (0-100%)
   - **Water Tax Rate**: Percentage (0-100%)
   - **Hunting Incentive**: Bonus units (0-100)
   - **Gathering Incentive**: Bonus units (0-100)
   - Quick reference to current policy settings

#### User Experience Flow

1. User navigates to Tribe Statistics page
2. Page loads list of available tribes
3. First tribe is auto-selected
4. Statistics are fetched and displayed
5. User can select different tribes from dropdown
6. Statistics automatically refresh when tribe changes
7. Error handling displays user-friendly messages if backend is unavailable

#### Data Fields Explained

| Field | Type | Description | Range/Values |
|-------|------|-------------|--------------|
| `tribeId` | Integer | Unique tribe identifier | 1+ |
| `tribeName` | String | Name of the tribe | User-defined |
| `currentTick` | Integer | Current simulation day | 0+ |
| `totalPopulation` | Integer | Total living members | 0+ |
| `roleBreakdown.hunters` | Integer | Number of hunters | 0+ |
| `roleBreakdown.gatherers` | Integer | Number of gatherers | 0+ |
| `roleBreakdown.children` | Integer | Number of children | 0+ |
| `roleBreakdown.elders` | Integer | Number of elders | 0+ |
| `healthStats.averageHealth` | Integer | Mean health of all members | 0-100 |
| `healthStats.minHealth` | Integer | Lowest health value | 0-100 |
| `healthStats.maxHealth` | Integer | Highest health value | 0-100 |
| `healthStats.healthyMembers` | Integer | Count with health ≥ 70 | 0+ |
| `resourceStats.food` | Integer | Total food units | 0+ |
| `resourceStats.water` | Integer | Total water units | 0+ |
| `resourceStats.resourceStatus` | String | Resource availability level | ABUNDANT, ADEQUATE, LOW, CRITICAL |
| `policySummary.foodTaxRate` | Integer | Food tax percentage | 0-100 |
| `policySummary.waterTaxRate` | Integer | Water tax percentage | 0-100 |
| `policySummary.huntingIncentive` | Integer | Hunting bonus units | 0-100 |
| `policySummary.gatheringIncentive` | Integer | Gathering bonus units | 0-100 |

### API Usage

#### Endpoint
```
GET /api/tribes/{id}/statistics
```

#### Example Request
```bash
curl http://localhost:8080/api/tribes/1/statistics
```

#### Example Response
```json
{
  "tribeId": 1,
  "tribeName": "Northern Tribe",
  "currentTick": 10,
  "totalPopulation": 6,
  "roleBreakdown": {
    "hunters": 2,
    "gatherers": 2,
    "children": 1,
    "elders": 1
  },
  "healthStats": {
    "averageHealth": 96,
    "minHealth": 80,
    "maxHealth": 100,
    "healthyMembers": 6
  },
  "resourceStats": {
    "food": 95,
    "water": 88,
    "resourceStatus": "ABUNDANT"
  },
  "policySummary": {
    "foodTaxRate": 10,
    "waterTaxRate": 10,
    "huntingIncentive": 5,
    "gatheringIncentive": 5
  }
}
```

#### Resource Status Calculation Logic

The `resourceStatus` field is calculated based on available resources per person:

```
foodPerPerson = totalFood / totalPopulation
waterPerPerson = totalWater / totalPopulation
minPerPerson = min(foodPerPerson, waterPerPerson)

if (minPerPerson >= 10):
    resourceStatus = "ABUNDANT"
else if (minPerPerson >= 5):
    resourceStatus = "ADEQUATE"
else if (minPerPerson >= 3):
    resourceStatus = "LOW"
else:
    resourceStatus = "CRITICAL"
```

---

## Manual Tick Execution

The Manual Tick Execution feature allows users to advance the simulation by one day (tick) on demand, providing immediate feedback on the results.

### What is a Tick?

A "tick" represents one day in the simulation. During a tick, the following processes occur:

1. **Resource Gathering** - Hunters and gatherers collect food and water based on their skills
2. **Central Storage Tax** - If enabled, a percentage goes to central storage
3. **Family Resource Consumption** - Each member consumes 3 food and 4 water
4. **Inter-Family Borrowing** - Families with insufficient resources attempt to borrow from others
5. **Central Storage Fallback** - Families can draw from central storage if borrowing fails
6. **Sharing Failure Consequences** - Members suffer health loss if resources are inadequate
7. **Storage Decay** - Periodic decay of stored resources (every N days)
8. **Skill Development** - Successful gathering improves member skills
9. **Aging** - Every 365 ticks, members age by 1 year
10. **Death** - Members with health = 0 are removed

### API Usage

#### Endpoint
```
POST /api/tribes/{id}/tick
```

#### Example Request
```bash
curl -X POST http://localhost:8080/api/tribes/1/tick
```

#### Example Response

The endpoint returns the complete updated tribe state after processing the tick:

```json
{
  "tribeId": 1,
  "tribeName": "Northern Tribe",
  "description": "A resilient tribe from the northern mountains",
  "currentTick": 11,
  "resources": {
    "food": 102,
    "water": 91
  },
  "policy": {
    "name": "Default Policy",
    "description": "Standard tribe policy",
    "foodTaxRate": 10,
    "waterTaxRate": 10,
    "huntingIncentive": 5,
    "gatheringIncentive": 5,
    "sharingPriority": "ELDER",
    "enableCentralStorage": false,
    "centralStorageTaxRate": 10,
    "storageDecayRate": 0.1,
    "storageDecayInterval": 20
  },
  "members": [
    {
      "id": 1,
      "name": "Hunter Alpha",
      "role": "HUNTER",
      "age": 25,
      "health": 100,
      "familyId": 1,
      "huntingSkill": 0.61,
      "gatheringSkill": 0.5
    },
    ...
  ],
  "families": [
    {
      "id": 1,
      "name": "Family A",
      "storage": {
        "food": 45,
        "water": 38
      }
    },
    ...
  ],
  "centralStorage": {
    "food": 0,
    "water": 0
  },
  "bondLevel": 50
}
```

### User Feedback

When a tick is executed, users receive comprehensive feedback through the response:

1. **Updated Tick Counter**: `currentTick` increments by 1
2. **Resource Changes**: New food and water totals for tribe and families
3. **Member Health Updates**: Each member's health may increase or decrease
4. **Skill Progression**: `huntingSkill` and `gatheringSkill` may improve
5. **Population Changes**: Members may die (removed from `members` array) if health reaches 0
6. **Bond Level Changes**: Tribe cohesion may increase or decrease based on sharing events
7. **Age Updates**: Every 365 ticks, member ages increment
8. **Role Transitions**: Children become adults at age 16, adults become elders at age 60

### Manual Tick Workflow Example

#### Step 1: Check Initial State
```bash
curl http://localhost:8080/api/tribes/1
```

Response shows:
- Current tick: 0
- Food: 100
- Water: 100
- 6 members with initial skills

#### Step 2: Execute First Tick
```bash
curl -X POST http://localhost:8080/api/tribes/1/tick
```

Response shows:
- Current tick: 1
- Food: 120 (increased due to gathering)
- Water: 95 (increased from gathering, decreased from consumption)
- Members' health at 100 (adequate resources)
- Hunters' hunting skills improved from 0.6 to 0.61

#### Step 3: Verify Changes
```bash
curl http://localhost:8080/api/tribes/1/statistics
```

Response shows updated statistics reflecting the tick's effects.

#### Step 4: Continue Simulation
Execute multiple ticks to observe:
```bash
# Execute 10 ticks in sequence
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/tribes/1/tick
  echo "Tick $i completed"
done
```

### Feedback Interpretation

| Change | Positive Indicator | Negative Indicator |
|--------|-------------------|-------------------|
| **Resources** | Food/water increasing | Food/water decreasing rapidly |
| **Health** | All members at 95-100 | Members below 70 |
| **Skills** | Gradual increase (0.6 → 0.7) | No change (unhealthy members) |
| **Population** | Stable member count | Members dying |
| **Bond Level** | Increasing (successful sharing) | Decreasing (failed sharing) |

### Error Handling

If the tribe doesn't exist:
```json
{
  "timestamp": "2025-10-26T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Tribe not found",
  "path": "/api/tribes/999/tick"
}
```

---

## Tribe Policy Management

The Tribe Policy Management feature allows users to configure how their tribe operates by adjusting tax rates, incentives, sharing priorities, storage settings, and decay parameters.

### Frontend UI/UX

The Tribe Policy Management page (`/policy` view) provides a comprehensive form interface for modifying tribe policies.

#### UI Components

1. **Page Header & Description**
   - Title: "Tribe Policy Management"
   - Description: Explains the purpose of policy configuration
   - User-friendly introduction to the feature

2. **Tribe Selector** (Dropdown)
   - Same as in Tribe Statistics
   - Lists all tribes with current day
   - Auto-selects first tribe
   - Triggers policy data reload on change

3. **Policy Form** (Multi-Section Form)
   - Organized into logical sections
   - Each field shows current value
   - Changed fields are visually highlighted
   - Input validation with range checks

4. **Form Actions** (Button Group)
   - **Reset Changes**: Reverts form to current saved values
   - **Update Policy**: Submits changes (disabled if no changes)
   - Loading state during submission
   - Change indicator message when unsaved changes exist

### Editable Policy Fields

The following table details all editable policy fields:

| Section | Field Name | Type | Range | Default | Description |
|---------|-----------|------|-------|---------|-------------|
| **Tax Rates** | `foodTaxRate` | Integer | 0-100 | 10 | Percentage of gathered food taken as tax (legacy, not actively used) |
| | `waterTaxRate` | Integer | 0-100 | 10 | Percentage of gathered water taken as tax (legacy, not actively used) |
| **Incentives** | `huntingIncentive` | Integer | 0-100 | 5 | Bonus food units awarded to hunters |
| | `gatheringIncentive` | Integer | 0-100 | 5 | Bonus resource units awarded to gatherers |
| **Sharing** | `sharingPriority` | String | ELDER, CHILD, HUNTER, GATHERER, YOUNGEST, RANDOM | ELDER | Who suffers first when resource sharing fails |
| **Central Storage** | `enableCentralStorage` | Boolean | true/false | false | Toggle central storage system |
| | `centralStorageTaxRate` | Integer | 0-100 | 10 | Percentage of gathered resources sent to central storage |
| **Decay** | `storageDecayRate` | Number | 0.0-1.0 | 0.1 | Fraction of storage lost per decay interval (e.g., 0.1 = 10%) |
| | `storageDecayInterval` | Integer | 1+ | 20 | Number of days between each decay event |

### Field Details

#### Tax Rates Section
- **Purpose**: Configure resource taxation (legacy feature, not actively used in current simulation)
- **Fields**:
  - Food Tax Rate: Percentage of food collected as tax
  - Water Tax Rate: Percentage of water collected as tax
- **Note**: These fields are maintained for compatibility but are not actively applied in the current simulation logic. Use `centralStorageTaxRate` instead.

#### Gathering Incentives Section
- **Purpose**: Encourage and reward resource gathering activities
- **Fields**:
  - Hunting Incentive: Bonus food units added to successful hunts
  - Gathering Incentive: Bonus units added to successful gathering
- **Impact**: Higher incentives lead to more resources collected per tick
- **Example**: With hunting incentive of 8, a hunter gathering 15 base food will receive 23 total

#### Resource Sharing Section
- **Purpose**: Determine priority order when families share resources
- **Field**: Sharing Priority (dropdown)
- **Options**:
  - **ELDER**: Oldest members or elders suffer first when sharing fails
  - **CHILD**: Youngest members or children suffer first
  - **HUNTER**: Hunters suffer first
  - **GATHERER**: Gatherers suffer first
  - **YOUNGEST**: Youngest member regardless of role suffers first
  - **RANDOM**: Random member selected
- **Impact**: Affects which family member loses 15 health when all sharing mechanisms fail

#### Central Storage Section
- **Purpose**: Enable tribe-level storage pool with centralized resource management
- **Fields**:
  - Enable Central Storage: Checkbox to activate the system
  - Central Storage Tax Rate: Percentage of gathered resources contributed
- **When Enabled**:
  - Gathered resources are taxed at the specified rate
  - Taxed resources go to central storage
  - Families can draw from central storage when depleted
- **When Disabled**: Families manage their own resources independently
- **Example**: With 20% tax rate, a hunter gathering 20 food contributes 4 to central storage and keeps 16

#### Storage Decay Section
- **Purpose**: Simulate realistic resource spoilage and degradation over time
- **Fields**:
  - Storage Decay Rate: Percentage lost per interval (0.0 = no decay, 1.0 = 100% loss)
  - Storage Decay Interval: Days between decay events
- **Impact**: 
  - All family storage decays periodically
  - Central storage also decays if enabled
  - Represents food spoilage and water evaporation
- **Example**: With 0.1 rate and 20-day interval, every 20 days all storage loses 10%

### User Experience Flow

#### Viewing Current Policy

1. User navigates to Policy Management page
2. Page loads available tribes
3. First tribe auto-selected
4. Current policy values populate form fields
5. Each field shows "Current: X" label for reference

#### Updating Policy

**Example Flow 1: Adjust Incentives**

1. User selects tribe "Northern Tribe"
2. Current hunting incentive: 5
3. User changes hunting incentive to 10
4. Field highlights as "changed"
5. "You have unsaved changes" message appears
6. User clicks "Update Policy"
7. Loading indicator shown
8. Success message: "Policy updated successfully!"
9. Form reloads with new values
10. Changed fields no longer highlighted

**Example Flow 2: Enable Central Storage**

1. User selects tribe
2. Central storage currently disabled
3. User checks "Enable Central Storage"
4. Central storage tax rate field becomes enabled
5. User sets tax rate to 15%
6. User clicks "Update Policy"
7. Success confirmation
8. Future ticks will now apply central storage taxation

**Example Flow 3: Reset Changes**

1. User modifies multiple fields
2. Decides not to save
3. Clicks "Reset Changes"
4. All fields revert to current saved values
5. No API request made
6. No changes persisted

### API Usage

#### Endpoint
```
PUT /api/tribes/{id}/policy
```

#### Partial Update Examples

**Example 1: Update Tax Rates Only**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "foodTaxRate": 15,
    "waterTaxRate": 20
  }'
```

**Example 2: Adjust Incentives**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "huntingIncentive": 10,
    "gatheringIncentive": 8
  }'
```

**Example 3: Enable Central Storage**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "enableCentralStorage": true,
    "centralStorageTaxRate": 20
  }'
```

**Example 4: Configure Storage Decay**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "storageDecayRate": 0.15,
    "storageDecayInterval": 10
  }'
```

**Example 5: Update Sharing Priority**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "sharingPriority": "CHILD"
  }'
```

**Example 6: Full Policy Update**
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{
    "foodTaxRate": 15,
    "waterTaxRate": 20,
    "huntingIncentive": 8,
    "gatheringIncentive": 10,
    "sharingPriority": "CHILD",
    "enableCentralStorage": true,
    "centralStorageTaxRate": 15,
    "storageDecayRate": 0.05,
    "storageDecayInterval": 30
  }'
```

#### Response Format

All policy update requests return the complete updated tribe state:

```json
{
  "tribeId": 1,
  "tribeName": "Northern Tribe",
  "description": "A resilient tribe from the northern mountains",
  "currentTick": 10,
  "resources": {
    "food": 95,
    "water": 88
  },
  "policy": {
    "name": "Default Policy",
    "description": "Standard tribe policy",
    "foodTaxRate": 15,
    "waterTaxRate": 20,
    "huntingIncentive": 8,
    "gatheringIncentive": 10,
    "sharingPriority": "CHILD",
    "enableCentralStorage": true,
    "centralStorageTaxRate": 15,
    "storageDecayRate": 0.05,
    "storageDecayInterval": 30
  },
  "members": [ ... ],
  "families": [ ... ],
  "centralStorage": { ... },
  "bondLevel": 50
}
```

### Input Validation

The frontend performs the following validations before submission:

| Field | Validation Rule | Error Message |
|-------|----------------|---------------|
| `foodTaxRate` | 0 ≤ value ≤ 100 | "Food tax rate must be between 0 and 100" |
| `waterTaxRate` | 0 ≤ value ≤ 100 | "Water tax rate must be between 0 and 100" |
| `huntingIncentive` | 0 ≤ value ≤ 100 | "Hunting incentive must be between 0 and 100" |
| `gatheringIncentive` | 0 ≤ value ≤ 100 | "Gathering incentive must be between 0 and 100" |
| `centralStorageTaxRate` | 0 ≤ value ≤ 100 | "Central storage tax rate must be between 0 and 100" |
| `storageDecayRate` | 0.0 ≤ value ≤ 1.0 | "Storage decay rate must be between 0 and 1" |
| `storageDecayInterval` | value ≥ 1 | "Storage decay interval must be at least 1" |

### Policy Update Scenarios

#### Scenario 1: Increase Resource Gathering

**Goal**: Boost tribe productivity

**Steps**:
1. Navigate to Policy Management
2. Increase hunting incentive to 15
3. Increase gathering incentive to 12
4. Update policy
5. Execute several ticks
6. Observe increased resource accumulation in statistics

**Expected Outcome**: More food and water gathered per tick

#### Scenario 2: Protect Children During Hardship

**Goal**: Prioritize children's survival during resource shortage

**Steps**:
1. Navigate to Policy Management
2. Change sharing priority to "ELDER"
3. Update policy
4. When resources run low, elders will suffer first
5. Children's health preserved

**Expected Outcome**: Elders lose health before children when sharing fails

#### Scenario 3: Enable Centralized Resource Management

**Goal**: Create tribal storage pool for emergencies

**Steps**:
1. Navigate to Policy Management
2. Enable central storage checkbox
3. Set central storage tax rate to 25%
4. Update policy
5. Execute ticks and observe central storage accumulating
6. When families run out, they can draw from central storage

**Expected Outcome**: Reduced family volatility, better resource distribution

#### Scenario 4: Reduce Storage Waste

**Goal**: Minimize resource decay due to spoilage

**Steps**:
1. Navigate to Policy Management
2. Reduce storage decay rate from 0.1 to 0.05 (10% → 5%)
3. Increase decay interval from 20 to 30 days
4. Update policy
5. Resources spoil less frequently and in smaller amounts

**Expected Outcome**: Longer-lasting resource reserves

### Frontend Interface Features

1. **Real-time Change Indicators**
   - Changed fields highlighted with CSS class
   - Visual feedback for modified values
   - Easy to see what's different before saving

2. **Current Value Display**
   - Each field shows "Current: X" label
   - Easy comparison between current and new values
   - Helps prevent accidental changes

3. **Organized Sections**
   - Tax Rates (legacy)
   - Gathering Incentives
   - Resource Sharing
   - Central Storage
   - Storage Decay
   - Logical grouping for easy navigation

4. **Conditional Field States**
   - Central storage tax rate disabled when central storage is off
   - Prevents invalid configurations
   - Smart form behavior

5. **Action Buttons**
   - Reset: Discard all changes
   - Update: Submit changes (disabled when no changes)
   - Loading state during submission
   - Clear user feedback

6. **Success/Error Messages**
   - Success: "Policy updated successfully!"
   - Error: Specific validation or connection errors
   - Clear communication of results

7. **Change Notice**
   - "You have unsaved changes" alert
   - Reminds user to save or reset
   - Prevents accidental data loss

---

## Feature Summary Table

| Feature | Frontend Page | Backend Endpoint | Key Capabilities | User Benefit |
|---------|--------------|------------------|------------------|--------------|
| **Tribe Statistics** | `/tribes` view | `GET /api/tribes/{id}/statistics` | View population, roles, health, resources, and policy summary | Quick assessment of tribe status and health |
| **Manual Tick Execution** | API only | `POST /api/tribes/{id}/tick` | Advance simulation by one day, receive complete updated state | Control simulation pace, observe immediate effects |
| **Policy Management** | `/policy` view | `PUT /api/tribes/{id}/policy` | Configure tax, incentives, sharing, storage, and decay | Customize tribe behavior and governance |
| **Tribe Creation** | API only | `POST /api/tribes` | Create new tribe with default settings | Start new simulations |
| **Tribe State** | API only | `GET /api/tribes/{id}` | Get complete tribe state with all members and families | Detailed inspection of tribe data |
| **Tribe List** | All views | `GET /api/tribes` | List all tribes in system | Manage multiple tribes |

### Feature Interactions

The three main features work together to provide a complete simulation experience:

1. **Create a Tribe** → Initial state established
2. **View Statistics** → Assess current state
3. **Update Policy** → Adjust tribe behavior
4. **Execute Ticks** → Advance simulation
5. **View Statistics** → Observe policy effects
6. **Iterate** → Refine policies based on results

### Quick Start Workflow

```bash
# 1. Create a tribe
curl -X POST http://localhost:8080/api/tribes \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Tribe","description":"Learning the features"}'

# 2. Check statistics
curl http://localhost:8080/api/tribes/1/statistics

# 3. Update policy (increase incentives)
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{"huntingIncentive":10,"gatheringIncentive":8}'

# 4. Execute a tick
curl -X POST http://localhost:8080/api/tribes/1/tick

# 5. Check statistics again to see effects
curl http://localhost:8080/api/tribes/1/statistics
```

### Frontend Navigation

Users can navigate between features using the header navigation:

- **Dashboard**: Overview and general information
- **Tribe Statistics**: View detailed tribe statistics
- **Policy Management**: Configure tribe policies

All pages feature the tribe selector dropdown for switching between tribes.

---

## Related Documentation

- **[Home](Home.md)** - Getting started and project overview
- **[API Guide](API-Guide.md)** - Complete REST API reference
- **[Hunter-Gatherer Simulation](Hunter-Gatherer-Simulation.md)** - Detailed simulation mechanics
- **[Technical Architecture](Technical-Architecture.md)** - System architecture and implementation

---

## Support and Feedback

For questions, issues, or feature requests, please refer to the project repository or contact the development team.
