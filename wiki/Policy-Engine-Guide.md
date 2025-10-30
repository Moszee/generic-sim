# Policy Engine Guide

## Overview

The **Policy Engine** is an extensible architecture for implementing game policies in a self-contained, composable, and maintainable way. Rather than embedding policy logic directly in service classes, each policy is implemented as a separate effect class that executes at specific phases of the simulation tick.

## Architecture

### Core Components

#### 1. PolicyPhase (Enumeration)

Defines the distinct phases where policy effects can execute during a simulation tick:

- **GATHERING** - Resource collection by tribe members
- **POST_GATHERING** - Processing after gathering (e.g., taxation)
- **FAMILY_UPKEEP** - Resource consumption and family needs
- **STORAGE_DECAY** - Periodic resource spoilage
- **AGING** - Age advancement and role transitions
- **PROGRESS_CALCULATION** - Technology/culture progress computation
- **CLEANUP** - End-of-tick cleanup and state reconciliation

#### 2. PolicyEffect (Interface)

The base interface for all policy effects. Each effect must implement:

```java
public interface PolicyEffect {
    PolicyPhase getPhase();        // Which phase to execute in
    int getPriority();              // Execution order (lower = earlier)
    boolean shouldApply(TickContext context);  // Filter condition
    void apply(TickContext context);           // Effect implementation
    String getName();               // Human-readable name
}
```

**Priority Ranges (Convention):**
- 0-99: Pre-processing and setup effects
- 100-199: Core game mechanics
- 200-299: Post-processing and adjustments
- 300+: Final cleanup and validation

#### 3. TickContext

A mutable context object passed to all effects during a tick, containing:

- The tribe being processed
- References to needed services (FamilyService)
- Shared random number generator
- Calculated values (elder count, bonuses)
- Family storage snapshots (for tracking gathered resources)

#### 4. PolicyEngine

The central engine that manages effect registration and execution:

```java
@Component
public class PolicyEngine {
    void registerEffect(PolicyEffect effect);
    void executePhase(PolicyPhase phase, TickContext context);
    // ... utility methods
}
```

Effects are organized by phase and sorted by priority. During tick processing, the engine executes all applicable effects for each phase in order.

## Creating a New Policy Effect

### Step 1: Implement PolicyEffect

Create a new class in the `com.genericsim.backend.policy.effects` package:

```java
package com.genericsim.backend.policy.effects;

import com.genericsim.backend.policy.*;
import org.springframework.stereotype.Component;

@Component
public class MyCustomEffect implements PolicyEffect {
    
    @Override
    public PolicyPhase getPhase() {
        // Choose the phase based on when your effect should execute:
        // - GATHERING: Modify gathering rates/mechanics
        // - POST_GATHERING: Process collected resources (taxes, storage)
        // - FAMILY_UPKEEP: Modify consumption or sharing
        // - STORAGE_DECAY: Apply periodic resource degradation
        // - AGING: Modify aging or role transitions
        // - PROGRESS_CALCULATION: Modify progress/culture generation
        // - CLEANUP: Final state reconciliation
        return PolicyPhase.POST_GATHERING;
    }
    
    @Override
    public int getPriority() {
        // Set priority (lower executes first)
        return 150;
    }
    
    @Override
    public boolean shouldApply(TickContext context) {
        // Check if this effect should run
        // Example: only apply if a specific policy setting is enabled
        // Note: Replace with your actual policy method
        var policy = context.getTribe().getPolicy();
        return policy != null && policy.isEnableCentralStorage(); // Use real policy method
    }
    
    @Override
    public void apply(TickContext context) {
        // Implement the effect logic
        var tribe = context.getTribe();
        
        // Read from context
        int elderCount = context.getElderCount();
        
        // Modify simulation state
        for (Family family : tribe.getFamilies()) {
            // Your logic here
        }
    }
    
    @Override
    public String getName() {
        return "MyCustomEffect";
    }
}
```

### Step 2: Register the Effect

Add your effect to `PolicyEngineConfiguration`:

```java
@Configuration
public class PolicyEngineConfiguration {
    
    private final PolicyEngine policyEngine;
    private final MyCustomEffect myCustomEffect;
    
    public PolicyEngineConfiguration(
            PolicyEngine policyEngine,
            MyCustomEffect myCustomEffect) {
        this.policyEngine = policyEngine;
        this.myCustomEffect = myCustomEffect;
    }
    
    @PostConstruct
    public void registerEffects() {
        policyEngine.registerEffect(myCustomEffect);
        // ... register other effects
    }
}
```

### Step 3: Write Tests

Create comprehensive tests for your effect:

```java
@SpringBootTest
public class MyCustomEffectTest {
    
    private MyCustomEffect effect;
    
    @Autowired
    private FamilyService familyService;
    
    @BeforeEach
    public void setUp() {
        effect = new MyCustomEffect();
    }
    
    @Test
    public void testPhaseAndPriority() {
        assertEquals(PolicyPhase.POST_GATHERING, effect.getPhase());
        assertEquals(150, effect.getPriority());
    }
    
    @Test
    public void testShouldApply() {
        // Test the filter condition
        Tribe tribe = new Tribe("Test", "Test");
        Policy policy = new Policy("Test", "Test", 10, 10, 5, 5);
        // Use actual policy method (replace with your policy's configuration)
        policy.setEnableCentralStorage(true);
        tribe.setPolicy(policy);
        
        TickContext context = new TickContext(tribe, familyService, new Random());
        assertTrue(effect.shouldApply(context));
    }
    
    @Test
    public void testApplyEffect() {
        // Test the effect logic
        // Setup tribe and context
        // Call effect.apply(context)
        // Assert expected changes
    }
}
```

## Example: Central Storage Tax Effect

The `CentralStorageTaxEffect` demonstrates a complete policy effect:

**Purpose:** Tax a percentage of gathered resources for central storage.

**Phase:** POST_GATHERING (after resources are collected)

**Priority:** 100 (core mechanic)

**Filter:** Only applies when central storage is enabled in policy

**Implementation:**
1. Iterate through all families
2. Calculate resources gathered this tick (using TickContext snapshots)
3. Compute tax based on policy tax rate
4. Transfer tax from family storage to central storage

**Key Features:**
- Uses `TickContext.getFoodGathered()` and `getWaterGathered()` to track what was gathered
- Only taxes new resources, not existing storage
- Respects policy configuration

## Best Practices

### 1. Single Responsibility
Each effect should do one thing well. Don't create "god effects" that handle multiple concerns.

### 2. Idempotent When Possible
Effects should produce consistent results when run multiple times with the same context (though they typically run once per tick).

### 3. Use TickContext for Shared State
Don't store mutable state in effect instances. Use TickContext for any data that needs to be shared across effects.

### 4. Efficient Filtering
Implement `shouldApply()` to quickly filter out unnecessary executions. This is called before `apply()`.

### 5. Document Phase and Priority Choices
Use comments to explain why you chose a specific phase and priority for your effect.

### 6. Test Edge Cases
Test with:
- Empty tribes/families
- Null/missing policy configurations
- Extreme values (0, negative, very large)
- Multiple ticks in sequence

## Effect Composition Rules

### Execution Order

Within a phase, effects execute in priority order (ascending). Effects with the same priority have undefined order.

### Phase Dependencies

Later phases can depend on earlier phases having completed. For example:
- STORAGE_DECAY depends on FAMILY_UPKEEP having consumed resources
- CLEANUP can safely remove deceased members after all other phases

### State Mutation

Effects can freely read and modify the tribe state. Changes are visible to subsequent effects in the same or later phases.

### Services

Effects can use injected services (via constructor) or services in TickContext. Prefer TickContext for tick-specific operations.

## Adding Policy Configuration Fields

When adding new policy types, update the `Policy` entity:

```java
@Entity
@Table(name = "policies")
public class Policy {
    // Example: Add a boolean feature flag
    // Replace 'hunterEfficiencyBoost' with your actual feature name
    @Column(nullable = false)
    private boolean enableHunterEfficiencyBoost = false;
    
    // Add getter/setter following JavaBean conventions
    public boolean isEnableHunterEfficiencyBoost() {
        return enableHunterEfficiencyBoost;
    }
    
    public void setEnableHunterEfficiencyBoost(boolean enabled) {
        this.enableHunterEfficiencyBoost = enabled;
    }
    
    // Example: Add a numeric configuration
    @Column(nullable = false)
    private int hunterEfficiencyBoostPercent = 10;
    
    public int getHunterEfficiencyBoostPercent() {
        return hunterEfficiencyBoostPercent;
    }
    
    public void setHunterEfficiencyBoostPercent(int percent) {
        this.hunterEfficiencyBoostPercent = percent;
    }
}
```

Then update DTOs and API endpoints to expose the new field.

## Future Extensions

The policy engine is designed to support:

1. **Boolean Policies** - Simple on/off toggles (currently supported)
2. **Numeric Policies** - Slider values like tax rates (currently supported)
3. **Enum Policies** - Multiple choice options (currently supported via SharingPriority)
4. **Complex Policies** - Multi-parameter effects with their own configuration objects
5. **Dynamic Policy Loading** - Runtime effect registration (not yet implemented)
6. **Policy Templates** - Predefined policy sets (not yet implemented)
7. **Policy Editor UI** - Frontend interface for designing policies (not yet implemented)

## Troubleshooting

### Effect Not Executing

1. Check `shouldApply()` returns true
2. Verify effect is registered in `PolicyEngineConfiguration`
3. Confirm effect is being called in the right phase
4. Check Spring component scanning includes your effect package

### Unexpected Execution Order

1. Verify priority values (lower = earlier)
2. Check if multiple effects have the same priority
3. Review phase dependencies

### State Not Persisting

1. Ensure `TribeService.processTick()` saves the tribe after execution
2. Check transaction boundaries
3. Verify JPA relationships are correctly mapped

## Related Documentation

- [Technical Architecture](Technical-Architecture.md) - Overall system design
- [Hunter-Gatherer Simulation](Hunter-Gatherer-Simulation.md) - Simulation model details
- [API Guide](API-Guide.md) - REST API for policy updates

## Summary

The Policy Engine provides:
- **Self-contained** effects that encapsulate all logic for a specific policy
- **Composable** effects that can be combined and reordered
- **Extensible** architecture for adding new policies without modifying existing code
- **Testable** effects with clear interfaces and dependencies
- **Maintainable** code with separation of concerns

This architecture scales from simple boolean toggles to complex multi-phase policy systems, providing a solid foundation for game evolution.
