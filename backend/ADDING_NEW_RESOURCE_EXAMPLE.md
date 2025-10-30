# Example: Adding a New Resource Without Code Changes

This document demonstrates how to add a new resource to the simulation by only editing the configuration file, with no code changes required.

## Scenario: Adding "Metal" as a New Resource

Let's add "Metal" (Kruszec) as a new resource type to the simulation.

### Step 1: Edit the Configuration File

Open `backend/src/main/resources/application.yml` and add the new resource to the `resources` section:

```yaml
simulation:
  resources:
    # ... existing resources (food, water, stone, wood) ...
    
    # NEW RESOURCE - Metal
    - id: "metal"
      type: "resource"
      name: "Kruszec"
      min: 0
      max: 1000
      defaultValue: 0
      production:
        mining: 0.3
        smelting: 0.5
      consumption:
        crafting: 1.0
        tool_making: 2.0
      decayRate: 0.0
      storageCapacity: 200
      weight: 3.0
      renewable: false
      description: "Advanced metal material for tools and weapons"
```

### Step 2: Restart the Application

```bash
cd backend
mvn spring-boot:run
```

### Step 3: Verify the New Resource is Available

Test via REST API:

```bash
# Get all resources
curl http://localhost:8080/api/config/resources | jq 'keys'

# Should now include "metal" in the list:
# ["food", "metal", "stone", "water", "wood"]

# Get metal configuration
curl http://localhost:8080/api/config/metal | jq .
```

Expected output:
```json
{
  "id": "metal",
  "name": "Kruszec",
  "type": "resource",
  "min": 0.0,
  "max": 1000.0,
  "defaultValue": 0.0,
  "production": {
    "mining": 0.3,
    "smelting": 0.5
  },
  "consumption": {
    "crafting": 1.0,
    "tool_making": 2.0
  },
  "affects": {},
  "description": "Advanced metal material for tools and weapons",
  "weight": 3.0,
  "decayRate": 0.0,
  "storageCapacity": 200,
  "renewable": false
}
```

### Step 4: Use the New Resource in Code

The new resource is immediately available in the storage system:

```java
@Autowired
private ResourceConfigService configService;

// Get metal configuration
ResourceOrCoefficientConfig metalConfig = configService.getConfig("metal");

// Use in storage
GenericResourceStorage storage = new GenericResourceStorage();
storage.initializeFromConfigs(configService.getAllConfigs());

// Work with metal
storage.setValue("metal", 50.0);
double metalAmount = storage.getValue("metal");
storage.addValue("metal", 20.0);
storage.removeValue("metal", 10.0);
```

## Example: Adding "Innovation" Coefficient

Let's add a new coefficient to track innovation in the community.

### Step 1: Edit Configuration

Add to the `coefficients` section in `application.yml`:

```yaml
simulation:
  coefficients:
    # ... existing coefficients (stability, morale, cohesion, demand, technology) ...
    
    # NEW COEFFICIENT - Innovation
    - id: "innovation"
      type: "coefficient"
      name: "Innowacyjność"
      min: 0
      max: 1
      defaultValue: 0.3
      production: {}
      consumption: {}
      affects:
        technology: 0.15
        morale: 0.05
      description: "Community's capacity for innovation and creativity"
```

### Step 2: Verify via API

```bash
curl http://localhost:8080/api/config/innovation | jq .
```

Expected output:
```json
{
  "id": "innovation",
  "name": "Innowacyjność",
  "type": "coefficient",
  "min": 0.0,
  "max": 1.0,
  "defaultValue": 0.3,
  "production": {},
  "consumption": {},
  "affects": {
    "technology": 0.15,
    "morale": 0.05
  },
  "description": "Community's capacity for innovation and creativity",
  "weight": 1.0,
  "decayRate": 0.0,
  "storageCapacity": 1000,
  "renewable": true
}
```

### Step 3: Use in Simulations

```java
// Innovation affects technology and morale
storage.setValue("innovation", 0.8);

// Get affects mapping from configuration
ResourceOrCoefficientConfig innovationConfig = configService.getConfig("innovation");
Map<String, Double> affects = innovationConfig.getAffects();

// Apply effects (this would typically be done by AI or simulation engine)
for (Map.Entry<String, Double> entry : affects.entrySet()) {
    String targetId = entry.getKey();
    double effectStrength = entry.getValue();
    double currentInnovation = storage.getValue("innovation");
    double currentTarget = storage.getValue(targetId);
    
    // Simple effect calculation: target += innovation * effectStrength
    storage.addValue(targetId, currentInnovation * effectStrength);
}
```

## Complete Example: Adding Multiple Related Items

Let's add a complete "pottery" system with a resource, coefficient, and their relationships.

### Configuration

```yaml
simulation:
  resources:
    # ... existing resources ...
    
    - id: "clay"
      type: "resource"
      name: "Glina"
      min: 0
      max: 3000
      defaultValue: 0
      production:
        gathering: 0.6
      consumption:
        pottery_making: 5.0
      decayRate: 0.0
      storageCapacity: 500
      weight: 1.8
      renewable: true
      description: "Soft earth used for pottery and construction"
    
    - id: "pottery"
      type: "resource"
      name: "Ceramika"
      min: 0
      max: 500
      defaultValue: 0
      production:
        pottery_making: 1.0
      consumption:
        trading: 0.5
        storage: 0.1
      decayRate: 0.02
      storageCapacity: 200
      weight: 2.0
      renewable: false
      description: "Crafted pottery items for storage and trade"
  
  coefficients:
    # ... existing coefficients ...
    
    - id: "craftsmanship"
      type: "coefficient"
      name: "Rzemiosło"
      min: 0
      max: 1
      defaultValue: 0.2
      production: {}
      consumption: {}
      affects:
        technology: 0.1
        morale: 0.08
        stability: 0.05
      description: "Skill level in crafts and artisan work"
```

### Usage Example

```java
// Initialize storage
GenericResourceStorage tribeStorage = new GenericResourceStorage();
tribeStorage.initializeFromConfigs(configService.getAllConfigs());

// Simulate pottery production cycle
double clayAmount = 100.0;
double craftsmanshipLevel = 0.7;

// Gather clay
tribeStorage.setValue("clay", clayAmount);

// Check if enough clay for pottery
ResourceOrCoefficientConfig clayConfig = configService.getConfig("clay");
double clayConsumption = clayConfig.getConsumption().get("pottery_making");

if (tribeStorage.hasValue("clay", clayConsumption)) {
    // Consume clay
    tribeStorage.removeValue("clay", clayConsumption);
    
    // Produce pottery (affected by craftsmanship)
    ResourceOrCoefficientConfig potteryConfig = configService.getConfig("pottery");
    double potteryProduction = potteryConfig.getProduction().get("pottery_making");
    double actualProduction = potteryProduction * craftsmanshipLevel;
    
    tribeStorage.addValue("pottery", actualProduction);
    
    // Craftsmanship improves with practice
    tribeStorage.addValue("craftsmanship", 0.01);
}

// Export current state
Map<String, Double> state = tribeStorage.exportState();
System.out.println("Clay: " + state.get("clay"));
System.out.println("Pottery: " + state.get("pottery"));
System.out.println("Craftsmanship: " + state.get("craftsmanship"));
```

## Testing New Additions

After adding new resources/coefficients, you can verify them with tests:

```java
@Test
void testNewMetalResource() {
    ResourceOrCoefficientConfig metal = configService.getConfig("metal");
    assertNotNull(metal);
    assertEquals("metal", metal.getId());
    assertEquals("resource", metal.getType());
    assertEquals(0.3, metal.getProduction().get("mining"));
    assertFalse(metal.isRenewable());
}

@Test
void testMetalStorage() {
    GenericResourceStorage storage = new GenericResourceStorage();
    storage.initializeFromConfigs(configService.getAllConfigs());
    
    storage.setValue("metal", 100.0);
    assertEquals(100.0, storage.getValue("metal"));
    
    assertTrue(storage.removeValue("metal", 30.0));
    assertEquals(70.0, storage.getValue("metal"));
}
```

## Key Points

1. **No code changes needed** - Just edit the YAML configuration
2. **Immediate availability** - New resources/coefficients are available after restart
3. **Full feature support** - New items work with storage, export/import, and all APIs
4. **Type safety** - Configuration is validated at startup
5. **Backward compatible** - Old resources continue to work

## Best Practices

1. **Choose meaningful IDs** - Use lowercase with underscores: `advanced_tools`, `social_cohesion`
2. **Set appropriate ranges** - Resources: 0-10000, Coefficients: 0-1
3. **Document thoroughly** - Always include clear descriptions
4. **Define relationships** - Use `affects` to model dependencies
5. **Balance gameplay** - Set realistic production/consumption rates
6. **Test thoroughly** - Add tests for new resources/coefficients

## Conclusion

The flexible resource and coefficient system allows you to extend the simulation without touching Java code. Simply edit the YAML configuration, restart the application, and your new resources/coefficients are ready to use!
