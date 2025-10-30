# Extending the Generic Simulation

This guide demonstrates how to extend the simulation with new resources, technologies, and lifestyles.

## Adding a New Resource

The generic model makes it extremely easy to add new resource types. Here's how to add "Metal" as a new resource:

### Step 1: Add to ResourceType Enum

Edit `backend/src/main/java/com/genericsim/backend/model/ResourceType.java`:

```java
public enum ResourceType {
    FOOD("Food", "Basic sustenance for survival"),
    WATER("Water", "Essential liquid for hydration"),
    STONE("Stone", "Basic building and tool material"),
    WOOD("Wood", "Basic construction and fuel material"),
    METAL("Metal", "Advanced material for tools and weapons");  // NEW!
    
    // ... rest of the enum code
}
```

### Step 2: (Optional) Configure Resource Definition

Edit `backend/src/main/java/com/genericsim/backend/service/DefinitionService.java`:

```java
private void initializeResourceDefinitions() {
    // ... existing resources ...
    
    // Metal - advanced material, rare
    if (resourceDefinitionRepository.findByResourceType(ResourceType.METAL).isEmpty()) {
        ResourceDefinition metal = new ResourceDefinition(ResourceType.METAL, 0.2, 0.0);
        metal.setDecayRate(0.0);        // Doesn't decay
        metal.setStorageCapacity(200);   // Lower capacity (heavy)
        metal.setWeight(3.0);            // Very heavy
        metal.setRenewable(false);       // Not renewable
        resourceDefinitionRepository.save(metal);
    }
}
```

### Step 3: Done!

That's it! The new resource is now:
- ✅ Available in all `ResourceStorage` instances
- ✅ Can be gathered, stored, and consumed
- ✅ Automatically included in database schema
- ✅ Available for use in technologies and lifestyles

No changes needed to:
- Controllers
- Services (except definition initialization)
- Database migrations
- Storage logic

## Adding a New Technology

### Step 1: Add to TechnologyType Enum

Edit `backend/src/main/java/com/genericsim/backend/model/TechnologyType.java`:

```java
public enum TechnologyType {
    FIRE("Fire", "Basic fire-making technology for cooking and warmth"),
    STONE_TOOLS("Stone Tools", "Basic tools made from stone"),
    AGRICULTURE("Agriculture", "Basic farming and crop cultivation"),
    ANIMAL_HUSBANDRY("Animal Husbandry", "Domestication and raising of animals"),
    METALWORKING("Metalworking", "Smelting and working with metal");  // NEW!
    
    // ... rest of the enum code
}
```

### Step 2: Configure Technology Definition

Edit `backend/src/main/java/com/genericsim/backend/service/DefinitionService.java`:

```java
private void initializeTechnologyDefinitions() {
    // ... existing technologies ...
    
    // Metalworking - requires stone tools and metal resources
    if (technologyDefinitionRepository.findByTechnologyType(TechnologyType.METALWORKING).isEmpty()) {
        TechnologyDefinition metalworking = new TechnologyDefinition(TechnologyType.METALWORKING, 300);
        metalworking.setEfficiencyBonus(0.25);
        metalworking.setStabilityBonus(15.0);
        metalworking.getPrerequisites().add(TechnologyType.STONE_TOOLS);
        metalworking.getPrerequisites().add(TechnologyType.FIRE);
        metalworking.getResourceCosts().put(ResourceType.STONE, 50);
        metalworking.getResourceCosts().put(ResourceType.METAL, 10);
        technologyDefinitionRepository.save(metalworking);
    }
}
```

### Step 3: Use in Code

```java
// Check if tribe has researched metalworking
if (tribe.hasTechnology(TechnologyType.METALWORKING)) {
    // Apply bonuses or unlock features
    double bonus = getTechnologyDefinition(TechnologyType.METALWORKING).getEfficiencyBonus();
}

// Unlock technology
tribe.addTechnology(TechnologyType.METALWORKING);
```

## Adding a New Lifestyle

### Step 1: Add to LifestyleType Enum

Edit `backend/src/main/java/com/genericsim/backend/model/LifestyleType.java`:

```java
public enum LifestyleType {
    HUNTER_GATHERER("Hunter-Gatherer", "Mobile communities that gather and hunt for food"),
    NOMADIC("Nomadic", "Mobile herding communities that follow resources"),
    SETTLED("Settled", "Permanent settlements with agriculture"),
    URBAN("Urban", "Large permanent cities with specialized labor");  // NEW!
    
    // ... rest of the enum code
}
```

### Step 2: Configure Lifestyle Definition

Edit `backend/src/main/java/com/genericsim/backend/service/DefinitionService.java`:

```java
private void initializeLifestyleDefinitions() {
    // ... existing lifestyles ...
    
    // Urban - requires multiple technologies
    if (lifestyleDefinitionRepository.findByLifestyleType(LifestyleType.URBAN).isEmpty()) {
        LifestyleDefinition urban = new LifestyleDefinition(LifestyleType.URBAN, 0.1, 1.5);
        urban.setMaintenanceCost(20.0);
        urban.getResourceGatheringModifiers().put(ResourceType.FOOD, 0.8);  // Less food gathering
        urban.getResourceGatheringModifiers().put(ResourceType.STONE, 1.8); // More construction
        urban.getResourceGatheringModifiers().put(ResourceType.METAL, 1.5); // More metalworking
        urban.getRequiredTechnologies().add(TechnologyType.AGRICULTURE);
        urban.getRequiredTechnologies().add(TechnologyType.METALWORKING);
        lifestyleDefinitionRepository.save(urban);
    }
}
```

### Step 3: Use in Code

```java
// Transition to urban lifestyle
if (tribe.hasTechnology(TechnologyType.AGRICULTURE) && 
    tribe.hasTechnology(TechnologyType.METALWORKING)) {
    tribe.setLifestyle(LifestyleType.URBAN);
}

// Apply lifestyle modifiers
LifestyleDefinition lifestyle = getLifestyleDefinition(tribe.getLifestyle());
double foodModifier = lifestyle.getResourceGatheringModifiers()
    .getOrDefault(ResourceType.FOOD, 1.0);
```

## Example: Complete Feature Addition

Let's add a complete pottery system with a new resource, technology, and lifestyle modifier:

### 1. Add Clay Resource

```java
// In ResourceType enum
CLAY("Clay", "Soft earth used for pottery")

// In DefinitionService
ResourceDefinition clay = new ResourceDefinition(ResourceType.CLAY, 0.6, 0.0);
clay.setDecayRate(0.0);
clay.setStorageCapacity(300);
clay.setWeight(1.8);
resourceDefinitionRepository.save(clay);
```

### 2. Add Pottery Technology

```java
// In TechnologyType enum
POTTERY("Pottery", "Creating containers from clay")

// In DefinitionService
TechnologyDefinition pottery = new TechnologyDefinition(TechnologyType.POTTERY, 120);
pottery.setEfficiencyBonus(0.12);
pottery.setStabilityBonus(8.0);
pottery.getPrerequisites().add(TechnologyType.FIRE);
pottery.getResourceCosts().put(ResourceType.CLAY, 30);
technologyDefinitionRepository.save(pottery);
```

### 3. Update Settled Lifestyle to Benefit from Pottery

```java
// In DefinitionService, settled lifestyle initialization
settled.getResourceGatheringModifiers().put(ResourceType.CLAY, 1.4);
```

### 4. Done!

The complete pottery system is now:
- ✅ Integrated with existing storage
- ✅ Part of the technology tree
- ✅ Affects lifestyle bonuses
- ✅ Stored in database
- ✅ Available via API

## Testing New Extensions

Always add tests for new features:

```java
@Test
void testMetalResourceType() {
    ResourceStorage storage = new ResourceStorage();
    storage.initializeDefaults();
    
    storage.setAmount(ResourceType.METAL, 50);
    assertEquals(50, storage.getAmount(ResourceType.METAL));
    
    assertTrue(storage.removeAmount(ResourceType.METAL, 30));
    assertEquals(20, storage.getAmount(ResourceType.METAL));
}

@Test
void testMetalworkingTechnology() {
    Tribe tribe = new Tribe("Test", "Test");
    
    assertFalse(tribe.hasTechnology(TechnologyType.METALWORKING));
    
    tribe.addTechnology(TechnologyType.METALWORKING);
    assertTrue(tribe.hasTechnology(TechnologyType.METALWORKING));
}
```

## Best Practices

1. **Keep enums organized** - Group related items together
2. **Set reasonable defaults** - Define appropriate costs, rates, and bonuses
3. **Document descriptions** - Use clear, descriptive strings for display names
4. **Test thoroughly** - Add unit tests for new functionality
5. **Consider balance** - Ensure new features don't break game balance
6. **Use prerequisites** - Create logical technology trees
7. **Apply modifiers wisely** - Lifestyle bonuses should make sense thematically

## Migration and Backward Compatibility

The system maintains backward compatibility with the legacy `Resources` class:
- Old code using `tribe.getResources().getFood()` continues to work
- New code can use `tribe.getGenericStorage().getAmount(ResourceType.FOOD)`
- Both are kept in sync during tribe creation

For new features, always use the generic system for maximum flexibility.
