# Generic Model Guide

## Overview

The Generic Model is a flexible, extensible framework for managing resources, technologies, and lifestyles in the simulation. It allows for easy addition of new elements without modifying core code.

## Core Components

### 1. Resource System

#### ResourceType Enum
Defines all available resource types in the simulation:
- **FOOD** - Basic sustenance for survival
- **WATER** - Essential liquid for hydration  
- **STONE** - Basic building and tool material
- **WOOD** - Basic construction and fuel material

**Adding a new resource type:**
Simply add a new value to the `ResourceType` enum:
```java
METAL("Metal", "Advanced material for tools and weapons")
```

#### ResourceDefinition
Configures characteristics for each resource type:
- `gatheringRate` - Base gathering rate multiplier (default: 1.0)
- `consumptionRate` - Per-person consumption per tick (default: 0.0)
- `decayRate` - Storage decay rate per interval (default: 0.1)
- `storageCapacity` - Maximum storage capacity (default: 1000)
- `weight` - Weight for carrying/transport (default: 1.0)
- `renewable` - Can be gathered/regenerated (default: true)

**Default configurations:**
- **Food**: consumption 3.0/day, decay 10%, renewable
- **Water**: consumption 4.0/day, decay 5%, renewable
- **Stone**: no consumption, no decay, heavy (weight 2.0)
- **Wood**: no consumption, decay 2%, medium weight (1.5)

#### ResourceStorage
Generic storage using `Map<ResourceType, Integer>`:
```java
ResourceStorage storage = new ResourceStorage();
storage.initializeDefaults(); // Initialize all resource types to 0
storage.setAmount(ResourceType.FOOD, 100);
storage.addAmount(ResourceType.STONE, 50);
boolean success = storage.removeAmount(ResourceType.WATER, 30);
```

**Key methods:**
- `getAmount(ResourceType)` - Get current amount
- `setAmount(ResourceType, int)` - Set amount (non-negative)
- `addAmount(ResourceType, int)` - Add to current amount
- `removeAmount(ResourceType, int)` - Remove amount (returns false if insufficient)
- `hasAmount(ResourceType, int)` - Check if sufficient amount exists

### 2. Technology System

#### TechnologyType Enum
Defines all available technologies:
- **FIRE** - Basic fire-making technology
- **STONE_TOOLS** - Basic tools made from stone
- **AGRICULTURE** - Basic farming and crop cultivation
- **ANIMAL_HUSBANDRY** - Domestication and raising of animals

**Adding a new technology:**
Add to the `TechnologyType` enum:
```java
POTTERY("Pottery", "Clay containers for storage")
```

#### TechnologyDefinition
Configures requirements and effects for each technology:
- `researchCost` - Progress points needed to unlock (default: 100)
- `stabilityBonus` - Bonus to tribe stability (default: 0.0)
- `efficiencyBonus` - Bonus to resource gathering efficiency (default: 0.0)
- `resourceCosts` - Map of required resources for unlocking
- `prerequisites` - Set of required technologies

**Default technology tree:**
```
FIRE (cost: 50)
  └─> STONE_TOOLS (cost: 100, requires: 20 stone)
      └─> AGRICULTURE (cost: 200)
          
ANIMAL_HUSBANDRY (cost: 150)
```

**Example technology progression:**
```java
tribe.addTechnology(TechnologyType.FIRE);
if (tribe.hasTechnology(TechnologyType.FIRE)) {
    // Can now research stone tools
    tribe.addTechnology(TechnologyType.STONE_TOOLS);
}
```

### 3. Lifestyle System

#### LifestyleType Enum
Defines different stages of societal development:
- **HUNTER_GATHERER** - Mobile communities that gather and hunt (default)
- **NOMADIC** - Mobile herding communities that follow resources
- **SETTLED** - Permanent settlements with agriculture

**Adding a new lifestyle:**
Add to the `LifestyleType` enum:
```java
URBAN("Urban", "Large permanent cities with specialized roles")
```

#### LifestyleDefinition
Configures characteristics for each lifestyle:
- `mobilityFactor` - How mobile the community is (0.0-1.0)
- `cohesionFactor` - Base community cohesion multiplier
- `maintenanceCost` - Base maintenance cost per tick
- `resourceGatheringModifiers` - Map of resource-specific gathering bonuses
- `requiredTechnologies` - Set of technologies needed to adopt lifestyle

**Default lifestyles:**

**Hunter-Gatherer** (starting lifestyle):
- Mobility: 1.0 (fully mobile)
- Cohesion: 0.8
- Maintenance: 0.0
- No technology requirements
- Base gathering for all resources

**Nomadic:**
- Mobility: 0.9
- Cohesion: 0.9
- Maintenance: 5.0
- Requires: Animal Husbandry
- +20% food gathering

**Settled:**
- Mobility: 0.2 (mostly stationary)
- Cohesion: 1.2 (stronger community bonds)
- Maintenance: 10.0
- Requires: Agriculture
- +50% food, +30% wood, +30% stone gathering

**Example lifestyle transition:**
```java
// Start as hunter-gatherers
tribe.setLifestyle(LifestyleType.HUNTER_GATHERER);

// Progress to nomadic when animal husbandry is unlocked
tribe.addTechnology(TechnologyType.ANIMAL_HUSBANDRY);
tribe.setLifestyle(LifestyleType.NOMADIC);

// Eventually settle down with agriculture
tribe.addTechnology(TechnologyType.AGRICULTURE);
tribe.setLifestyle(LifestyleType.SETTLED);
```

## Database Schema

### New Tables
- `resource_definitions` - Stores resource configurations
- `technology_definitions` - Stores technology configurations  
- `lifestyle_definitions` - Stores lifestyle configurations
- `resource_storage` - Stores generic resource amounts
- `resource_storage_amounts` - ElementCollection for resource amounts
- `technology_resource_costs` - ElementCollection for technology costs
- `technology_prerequisites` - ElementCollection for technology prerequisites
- `lifestyle_resource_modifiers` - ElementCollection for lifestyle modifiers
- `lifestyle_required_technologies` - ElementCollection for lifestyle requirements
- `tribe_technologies` - ElementCollection for tribe's unlocked technologies

### Updated Tables
- `tribes` - Added `lifestyle`, `generic_storage_id`, `generic_central_storage_id`
- `families` - Added `generic_storage_id`

## Service Layer

### DefinitionService
Manages resource, technology, and lifestyle definitions. Initializes defaults on startup via `@PostConstruct`.

**Key methods:**
```java
ResourceDefinition getResourceDefinition(ResourceType type)
TechnologyDefinition getTechnologyDefinition(TechnologyType type)
LifestyleDefinition getLifestyleDefinition(LifestyleType type)
```

Definitions are automatically created if they don't exist, ensuring the system always has valid configurations.

## Integration with Existing System

### Backward Compatibility
The existing `Resources` class (with separate `food` and `water` fields) is maintained alongside the new `ResourceStorage` system. Both are populated during tribe creation:

```java
// Legacy system (for backward compatibility)
tribe.setResources(new Resources(100, 100));

// New generic system
ResourceStorage storage = new ResourceStorage();
storage.initializeDefaults();
storage.setAmount(ResourceType.FOOD, 100);
storage.setAmount(ResourceType.WATER, 100);
tribe.setGenericStorage(storage);
```

### Family Storage
Each family now has both legacy and generic storage:
```java
family.getStorage(); // Legacy Resources object
family.getGenericStorage(); // New ResourceStorage with all types
```

## Benefits

### 1. Easy Extensibility
Adding new resources requires only:
1. Add enum value to `ResourceType`
2. Optionally customize definition in `DefinitionService.initializeResourceDefinitions()`

No changes needed to:
- Storage logic
- Database schema
- Service layer
- Controller endpoints

### 2. Configuration-Driven
All coefficients, costs, and bonuses are stored in definitions, allowing:
- Easy balancing without code changes
- Future configuration via JSON/YAML
- Runtime modification of game parameters

### 3. Technology Trees
Natural support for prerequisite chains and resource costs:
```java
TechnologyDefinition pottery = new TechnologyDefinition(TechnologyType.POTTERY, 150);
pottery.getPrerequisites().add(TechnologyType.FIRE);
pottery.getResourceCosts().put(ResourceType.STONE, 30);
```

### 4. Lifestyle Progression
Clear path for community development:
```
Hunter-Gatherer → Nomadic (with Animal Husbandry)
                ↘ Settled (with Agriculture)
```

### 5. Dynamic Modifiers
Lifestyles can modify resource gathering without changing gathering logic:
```java
double baseGathering = 10.0;
LifestyleDefinition lifestyle = getLifestyleDefinition(tribe.getLifestyle());
double modifier = lifestyle.getResourceGatheringModifiers()
    .getOrDefault(ResourceType.FOOD, 1.0);
double actualGathering = baseGathering * modifier;
```

## Future Enhancements

Potential extensions to the model:
1. **Resource transformations** - Convert wood to charcoal, stone to tools
2. **Complex recipes** - Technologies requiring multiple resources
3. **Dynamic definitions** - Load from JSON/YAML configuration files
4. **Resource quality levels** - Poor/Normal/Excellent variants
5. **Seasonal modifiers** - Different gathering rates per season
6. **Trade system** - Inter-tribe resource exchange
7. **Specializations** - Individual or family resource specialties
8. **Building system** - Permanent structures requiring resources
9. **Tool degradation** - Tools wear out over time, require maintenance
10. **Resource discovery** - New resource types unlocked through exploration

## Testing

The model includes comprehensive tests:
- **ResourceStorageTest** - Tests generic storage operations
- **GenericModelTest** - Tests enums, definitions, and tribe integration
- **DefinitionServiceTest** - Tests initialization and persistence

All 91 tests pass, including 23 new tests for the generic model.

## Migration Path

For future migration from legacy to fully generic system:

1. **Phase 1** (Current): Dual system - both legacy and generic storage
2. **Phase 2**: Migrate gathering logic to use `ResourceStorage`
3. **Phase 3**: Migrate consumption logic to use `ResourceStorage`
4. **Phase 4**: Update DTOs to expose generic storage
5. **Phase 5**: Remove legacy `Resources` class
6. **Phase 6**: Update database to remove legacy columns

The current implementation provides full backward compatibility while enabling the new features.
