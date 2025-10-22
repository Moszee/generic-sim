# API Guide - Hunter-Gatherer Tribe Simulation

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
