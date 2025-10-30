# Resource and Coefficient Configuration Guide

## Overview

The Generic Simulation supports a fully flexible and extensible model for resources and coefficients. Instead of hardcoding resource types in Java enums, all resources and coefficients are defined in YAML/JSON configuration files.

## Key Benefits

- ✅ **No code changes required** - Add new resources/coefficients by editing configuration files only
- ✅ **Full configurability** - Control min/max values, production/consumption rates, and inter-dependencies
- ✅ **Support for both resources and coefficients** - Manage tangible resources (food, water) and abstract metrics (stability, morale)
- ✅ **Export/Import capability** - Save and restore simulation state as JSON
- ✅ **AI-ready** - Dependencies and effects can be calculated by AI layer based on state snapshots

## Configuration File Location

The configuration is loaded from:
```
backend/src/main/resources/application.yml
```

## Configuration Format

### Top-Level Structure

```yaml
simulation:
  resources:
    - # List of resource configurations
  coefficients:
    - # List of coefficient configurations
```

### Resource Configuration

Resources are tangible items that can be gathered, stored, and consumed (e.g., food, water, stone, wood).

```yaml
resources:
  - id: "food"                      # Unique identifier (required)
    type: "resource"                # Type: "resource" or "coefficient" (optional, defaults to "resource")
    name: "Jedzenie"                # Display name for UI (required)
    min: 0                          # Minimum allowed value (optional, default: 0)
    max: 10000                      # Maximum allowed value (optional, default: 10000)
    defaultValue: 100               # Initial/default value (optional, default: 0)
    description: "Basic sustenance" # Human-readable description (optional)
    
    # Resource-specific properties
    production:                     # Production rates by source (optional)
      gathering: 1.0                # Rate multiplier for gathering
      farming: 2.0                  # Rate multiplier for farming
    
    consumption:                    # Consumption rates by consumer (optional)
      family: 3.0                   # Per-family consumption per tick
      event: 1.0                    # Event-based consumption
    
    affects: {}                     # Effects on other resources/coefficients (optional)
    
    decayRate: 0.1                  # Storage decay rate per interval (optional, default: 0)
    storageCapacity: 1000           # Maximum storage capacity (optional, default: 1000)
    weight: 1.0                     # Weight for transport (optional, default: 1.0)
    renewable: true                 # Can be gathered/regenerated (optional, default: true)
```

### Coefficient Configuration

Coefficients are abstract metrics representing community state (e.g., stability, morale, cohesion).

```yaml
coefficients:
  - id: "stability"                 # Unique identifier (required)
    type: "coefficient"             # Type: "resource" or "coefficient" (optional, defaults to "coefficient")
    name: "Stabilność"              # Display name for UI (required)
    min: 0                          # Minimum allowed value (optional, default: 0)
    max: 1                          # Maximum allowed value (optional, default: 10000)
    defaultValue: 0.5               # Initial/default value (optional, default: 0)
    description: "Community stability" # Human-readable description (optional)
    
    production: {}                  # Production rates (optional, usually empty for coefficients)
    consumption: {}                 # Consumption rates (optional, usually empty for coefficients)
    
    affects:                        # Effects on other coefficients (optional)
      morale: 0.1                   # Positive effect: +0.1 to morale
      cohesion: -0.05               # Negative effect: -0.05 to cohesion
```

## Complete Example

```yaml
simulation:
  resources:
    - id: "food"
      type: "resource"
      name: "Jedzenie"
      min: 0
      max: 10000
      defaultValue: 100
      production:
        gathering: 1.0
        farming: 2.0
      consumption:
        family: 3.0
        event: 1.0
      decayRate: 0.1
      storageCapacity: 1000
      weight: 1.0
      renewable: true
      description: "Basic sustenance for survival"
      
    - id: "water"
      type: "resource"
      name: "Woda"
      min: 0
      max: 10000
      defaultValue: 100
      production:
        gathering: 1.0
      consumption:
        family: 4.0
      decayRate: 0.05
      storageCapacity: 1000
      weight: 1.0
      renewable: true
      description: "Essential liquid for hydration"
  
  coefficients:
    - id: "stability"
      type: "coefficient"
      name: "Stabilność"
      min: 0
      max: 1
      defaultValue: 0.5
      production: {}
      consumption: {}
      affects: {}
      description: "Overall stability and order of the community"
      
    - id: "morale"
      type: "coefficient"
      name: "Morale"
      min: 0
      max: 1
      defaultValue: 0.5
      production: {}
      consumption: {}
      affects:
        stability: 0.2
      description: "Spirit and motivation of the community"
```

## Data Model

### Configuration Classes

**ResourceOrCoefficientConfig** - Represents a configuration loaded from YAML:
```java
public class ResourceOrCoefficientConfig {
    private String id;                          // Unique identifier
    private String name;                        // Display name
    private String type;                        // "resource" or "coefficient"
    private double min;                         // Minimum value
    private double max;                         // Maximum value
    private double defaultValue;                // Initial value
    private Map<String, Double> production;     // Production rates
    private Map<String, Double> consumption;    // Consumption rates
    private Map<String, Double> affects;        // Effects on others
    private String description;                 // Description
    private double weight;                      // Weight (resources only)
    private double decayRate;                   // Decay rate (resources only)
    private int storageCapacity;                // Storage capacity (resources only)
    private boolean renewable;                  // Renewable flag (resources only)
}
```

### Runtime Storage

**GenericResourceStorage** - Stores runtime values:
```java
public class GenericResourceStorage {
    private Map<String, Double> values;         // Resource/coefficient ID -> current value
    
    // Methods
    public double getValue(String resourceId);
    public void setValue(String resourceId, double value);
    public void addValue(String resourceId, double amount);
    public boolean removeValue(String resourceId, double amount);
    public Map<String, Double> exportState();
    public void importState(Map<String, Double> state);
}
```

## API Endpoints

### Get All Configurations
```bash
GET /api/config
```
Returns all resource and coefficient configurations.

### Get Specific Configuration
```bash
GET /api/config/{id}
```
Returns configuration for a specific resource or coefficient by ID.

Example:
```bash
curl http://localhost:8080/api/config/food
```

### Get All Resource Configurations
```bash
GET /api/config/resources
```
Returns only resource configurations.

### Get All Coefficient Configurations
```bash
GET /api/config/coefficients
```
Returns only coefficient configurations.

## Usage Examples

### 1. Adding a New Resource

To add "Metal" as a new resource, simply add to `application.yml`:

```yaml
simulation:
  resources:
    # ... existing resources ...
    
    - id: "metal"
      type: "resource"
      name: "Metal"
      min: 0
      max: 1000
      defaultValue: 0
      production:
        mining: 0.3
      consumption: {}
      decayRate: 0.0
      storageCapacity: 200
      weight: 3.0
      renewable: false
      description: "Advanced material for tools and weapons"
```

**No code changes required!** The new resource is immediately available after restarting the application.

### 2. Adding a New Coefficient

To add "Technology Level" as a new coefficient:

```yaml
simulation:
  coefficients:
    # ... existing coefficients ...
    
    - id: "technology_level"
      type: "coefficient"
      name: "Poziom Technologii"
      min: 0
      max: 100
      defaultValue: 0
      production: {}
      consumption: {}
      affects:
        stability: 0.1
        morale: 0.05
      description: "Technological advancement of the community"
```

### 3. Programmatic Access

```java
@Autowired
private ResourceConfigService configService;

@Autowired
private GenericResourceStorage storage;

// Get configuration
ResourceOrCoefficientConfig foodConfig = configService.getConfig("food");

// Initialize storage with defaults
storage.initializeFromConfigs(configService.getAllConfigs());

// Work with resources
storage.setValue("food", 100.0);
double currentFood = storage.getValue("food");
storage.addValue("food", 50.0);

// Work with coefficients
storage.setValue("stability", 0.8);
double stability = storage.getValue("stability");

// Export state
Map<String, Double> state = storage.exportState();

// Import state
storage.importState(state);
```

### 4. Export/Import State

Export current state:
```java
Map<String, Double> state = storage.exportState();
// Returns: {"food": 100.0, "water": 50.0, "stability": 0.8, ...}
```

Import state:
```java
Map<String, Double> state = new HashMap<>();
state.put("food", 150.0);
state.put("stability", 0.9);
storage.importState(state);
```

## Best Practices

1. **Use descriptive IDs** - Use lowercase with underscores: `food`, `stone_tools`, `technology_level`
2. **Set appropriate ranges** - Resources typically 0-10000, coefficients 0-1
3. **Document with descriptions** - Always include a clear description field
4. **Define relationships** - Use `affects` to model dependencies between coefficients
5. **Configure production/consumption** - Define realistic rates for game balance
6. **Consider decay rates** - Perishable resources (food, water) should have decay > 0

## Migration from Old System

The old hardcoded `ResourceType` enum and `Resources` entity are still supported for backward compatibility. However, new features should use the flexible configuration system.

Old way:
```java
ResourceType.FOOD
tribe.getResources().getFood()
```

New way:
```java
"food"
storage.getValue("food")
```

## AI Integration

The configuration system is designed to work with AI layers:

1. AI can query current state via `storage.exportState()`
2. AI analyzes dependencies defined in `affects` mappings
3. AI calculates complex interactions between resources and coefficients
4. AI updates values via `storage.setValue()` based on computed effects

Example AI workflow:
```java
// Get current state
Map<String, Double> currentState = storage.exportState();

// AI analyzes and computes new values
Map<String, Double> newState = aiLayer.computeNextState(currentState, configService.getAllConfigs());

// Apply AI-computed changes
for (Map.Entry<String, Double> entry : newState.entrySet()) {
    storage.setValue(entry.getKey(), entry.getValue());
}
```

## Troubleshooting

### Configuration not loading
- Verify `application.yml` is in `src/main/resources`
- Check YAML syntax (proper indentation, no tabs)
- Ensure all required fields (`id`, `name`) are present

### Values not persisting
- Ensure `GenericResourceStorage` is properly saved via JPA
- Check that changes are committed in a transaction

### ID conflicts
- Each resource/coefficient must have a unique `id`
- IDs are case-sensitive

## Future Enhancements

Planned features:
- External JSON file loading (in addition to YAML)
- Hot-reload of configurations without restart
- Validation rules in configuration
- Conditional effects based on thresholds
- Time-based production/consumption curves
