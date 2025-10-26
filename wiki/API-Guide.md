# API Guide - Hunter-Gatherer Tribe Simulation

> **💡 New to the simulation?** Check out the **[Features Guide](Features-Guide.md)** for detailed information on tribe statistics, manual tick execution, and policy management with UI/UX details and usage examples.

## Base URL
```
http://localhost:8080/api
```

## Endpoints

### Health Check

Check if the backend is running.

**Endpoint:** `GET /health`

**Response:**
```json
{
  "status": "UP",
  "message": "Generic Simulation Backend is running"
}
```

---

## Tribe Management

### Create a Tribe

Create a new hunter-gatherer tribe with initial members and resources.

**Endpoint:** `POST /tribes`

**Request Body:**
```json
{
  "name": "Northern Tribe",
  "description": "A resilient tribe from the northern mountains"
}
```

**Response:** `200 OK`
```json
{
  "tribeId": 1,
  "tribeName": "Northern Tribe",
  "description": "A resilient tribe from the northern mountains",
  "currentTick": 0,
  "resources": {
    "food": 100,
    "water": 100
  },
  "policy": {
    "name": "Default Policy",
    "description": "Standard tribe policy",
    "foodTaxRate": 10,
    "waterTaxRate": 10,
    "huntingIncentive": 5,
    "gatheringIncentive": 5
  },
  "members": [
    {
      "id": 1,
      "name": "Hunter Alpha",
      "role": "HUNTER",
      "age": 25,
      "health": 100
    },
    {
      "id": 2,
      "name": "Hunter Beta",
      "role": "HUNTER",
      "age": 28,
      "health": 100
    },
    {
      "id": 3,
      "name": "Gatherer Alpha",
      "role": "GATHERER",
      "age": 24,
      "health": 100
    },
    {
      "id": 4,
      "name": "Gatherer Beta",
      "role": "GATHERER",
      "age": 26,
      "health": 100
    },
    {
      "id": 5,
      "name": "Child Alpha",
      "role": "CHILD",
      "age": 8,
      "health": 100
    },
    {
      "id": 6,
      "name": "Elder Wise",
      "role": "ELDER",
      "age": 65,
      "health": 80
    }
  ]
}
```

---

### Get All Tribes

Retrieve a list of all tribes in the system.

**Endpoint:** `GET /tribes`

**Response:** `200 OK`
```json
[
  {
    "tribeId": 1,
    "tribeName": "Northern Tribe",
    "description": "A resilient tribe from the northern mountains",
    "currentTick": 5,
    "resources": {
      "food": 120,
      "water": 95
    },
    "policy": { ... },
    "members": [ ... ]
  },
  {
    "tribeId": 2,
    "tribeName": "Southern Tribe",
    "description": "A coastal tribe",
    "currentTick": 3,
    "resources": {
      "food": 85,
      "water": 110
    },
    "policy": { ... },
    "members": [ ... ]
  }
]
```

---

### Get Tribe State

Retrieve the current state of a specific tribe.

**Endpoint:** `GET /tribes/{id}`

**Parameters:**
- `id` (path) - The tribe ID

**Response:** `200 OK`
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
    "foodTaxRate": 10,
    "waterTaxRate": 10,
    "huntingIncentive": 5,
    "gatheringIncentive": 5
  },
  "members": [
    {
      "id": 1,
      "name": "Hunter Alpha",
      "role": "HUNTER",
      "age": 25,
      "health": 100
    },
    ...
  ]
}
```

---

### Get Tribe Statistics

Retrieve aggregated statistics for a tribe in a frontend-friendly format.

**Endpoint:** `GET /tribes/{id}/statistics`

**Parameters:**
- `id` (path) - The tribe ID

**Response:** `200 OK`
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

**Resource Status Values:**
- `ABUNDANT` - 10+ food and water per person
- `ADEQUATE` - 5-9 food and water per person
- `LOW` - 3-4 food and water per person
- `CRITICAL` - Less than 3 food or water per person

**Frontend Display:**

The frontend provides a dedicated Tribe Statistics view that displays this data in a user-friendly format:

1. **Tribe Selector**: Dropdown menu to select from available tribes
2. **Population Overview**: Shows total population and current simulation day
3. **Role Distribution**: Visual breakdown of hunters, gatherers, children, and elders
4. **Health Statistics**: Displays average, minimum, maximum health, and count of healthy members (health ≥ 70)
5. **Resource Status**: Color-coded badge indicating resource availability level:
   - **Green (ABUNDANT)**: Plentiful resources, tribe is thriving
   - **Yellow (ADEQUATE)**: Sufficient resources, tribe is stable  
   - **Orange (LOW)**: Resources running low, action may be needed
   - **Red (CRITICAL)**: Severe resource shortage, tribe at risk
6. **Policy Summary**: Current tax rates and incentive settings

The view automatically refreshes when switching between tribes and provides per-person resource calculations for easy assessment of tribe health.

---

### Update Tribe Policy

Update the policy settings for a tribe. The frontend provides a Policy Management interface for making these changes.

**Endpoint:** `PUT /tribes/{id}/policy`

**Parameters:**
- `id` (path) - The tribe ID

**Request Body:**

All fields are optional. Only provided fields will be updated (partial updates supported).

**Available Policy Fields:**
- `foodTaxRate` (integer, 0-100): Percentage of food collected as tax
- `waterTaxRate` (integer, 0-100): Percentage of water collected as tax
- `huntingIncentive` (integer, 0-100): Bonus resources for hunting activities
- `gatheringIncentive` (integer, 0-100): Bonus resources for gathering activities
- `sharingPriority` (string): Priority order for resource sharing
  - Values: `ELDER`, `CHILD`, `HUNTER`, `GATHERER`, `YOUNGEST`, `RANDOM`
- `enableCentralStorage` (boolean): Enable tribe-level central storage pool
- `centralStorageTaxRate` (integer, 0-100): Additional tax for central storage
- `storageDecayRate` (number, 0-1): Fraction of stored resources lost per decay interval
- `storageDecayInterval` (integer, min 1): Days between each decay event

**Example - Full Update:**
```json
{
  "foodTaxRate": 15,
  "waterTaxRate": 20,
  "huntingIncentive": 8,
  "gatheringIncentive": 10,
  "sharingPriority": "CHILD",
  "enableCentralStorage": true,
  "centralStorageTaxRate": 15,
  "storageDecayRate": 0.05,
  "storageDecayInterval": 30
}
```

**Example - Partial Update (Tax Rates Only):**
```json
{
  "foodTaxRate": 15,
  "waterTaxRate": 20
}
```

**Example - Enable Central Storage:**
```json
{
  "enableCentralStorage": true,
  "centralStorageTaxRate": 20
}
```

**Example - Adjust Resource Decay:**
```json
{
  "storageDecayRate": 0.15,
  "storageDecayInterval": 10
}
```

**Response:** `200 OK`
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
  "members": [ ... ]
}
```

**Frontend Interface:**

The Policy Management view in the frontend application provides a user-friendly interface for updating policies:

1. **Navigate to Policy Management** from the main navigation menu
2. **Select a tribe** from the dropdown selector
3. **View current policy values** displayed alongside each field
4. **Modify desired policy fields** - changed values are highlighted
5. **Preview changes** before submitting
6. **Submit updates** or reset to original values
7. **Receive feedback** on success or validation errors

The interface includes:
- Input validation with range checks
- Real-time change indicators
- Descriptive field labels and hints
- Organized sections: Tax Rates, Incentives, Sharing, Central Storage, and Decay
- Ability to enable/disable central storage with dependent fields

---

### Process Simulation Tick

Advance the simulation by one day (tick) for a specific tribe.

**Endpoint:** `POST /tribes/{id}/tick`

**Parameters:**
- `id` (path) - The tribe ID

**Response:** `200 OK`
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
  "policy": { ... },
  "members": [ ... ]
}
```

**What happens during a tick:**
1. Tribe members gather resources based on their roles
2. Taxes are applied to gathered resources
3. Resources are consumed by the population
4. Member health is updated based on resource availability
5. Members age (every 365 ticks)
6. Dead members (health = 0) are removed

---

## Testing with cURL

### Create a tribe:
```bash
curl -X POST http://localhost:8080/api/tribes \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Tribe","description":"My test tribe"}'
```

### Get all tribes:
```bash
curl http://localhost:8080/api/tribes
```

### Get tribe state:
```bash
curl http://localhost:8080/api/tribes/1
```

### Get tribe statistics:
```bash
curl http://localhost:8080/api/tribes/1/statistics
```

### Update tribe policy (partial update):
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{"foodTaxRate":15,"huntingIncentive":10}'
```

### Update tribe policy (full update with all fields):
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

### Enable central storage:
```bash
curl -X PUT http://localhost:8080/api/tribes/1/policy \
  -H "Content-Type: application/json" \
  -d '{"enableCentralStorage":true,"centralStorageTaxRate":20}'
```

### Process a tick:
```bash
curl -X POST http://localhost:8080/api/tribes/1/tick
```

---

## Error Responses

### Tribe Not Found
**Status:** `500 Internal Server Error`
```json
{
  "timestamp": "2025-10-22T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Tribe not found",
  "path": "/api/tribes/999"
}
```

---

## Automated Processing

The system includes a scheduled job that automatically processes a tick for all tribes every day at midnight (00:00:00). This means tribes will continue to evolve even without manual API calls.

You can monitor this through the application logs:
```
Processing daily tick for all tribes
Processed tick for tribe: Northern Tribe (ID: 1)
Daily tick processing completed. Processed 2 tribes
```

---

## Related Documentation

For more detailed information on using these features:

- **[Features Guide](Features-Guide.md)** - Comprehensive guide covering tribe statistics display, manual tick execution workflows, and policy management with UI/UX details
- **[Hunter-Gatherer Simulation](Hunter-Gatherer-Simulation.md)** - Deep dive into simulation mechanics and logic
- **[Home](Home.md)** - Getting started and project overview
