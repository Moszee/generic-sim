package com.genericsim.backend.policy;

import com.genericsim.backend.model.Family;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.service.FamilyService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Context object passed through all policy effects during a simulation tick.
 * Contains the tribe state and services needed for policy execution.
 * 
 * This context is mutable and serves as the primary mechanism for policy effects
 * to read and modify simulation state.
 */
@Getter
@Setter
public class TickContext {
    /**
     * The tribe being processed in this tick.
     */
    private final Tribe tribe;
    
    /**
     * Service for family-related operations.
     */
    private final FamilyService familyService;
    
    /**
     * Random number generator for stochastic effects.
     * Shared across all effects for consistency.
     */
    private final Random random;
    
    /**
     * Elder count calculated at the start of the tick.
     * Used for elder-based bonuses in multiple effects.
     */
    private int elderCount;
    
    /**
     * Gathering bonus multiplier based on elder count.
     * Calculated once and reused by gathering effects.
     */
    private double elderGatheringBonus;
    
    /**
     * Tracks family storage levels before gathering started.
     * Used by effects that need to know what was gathered this tick.
     * Uses family object as key to support both persisted and non-persisted families.
     */
    private final Map<Family, FamilyResourceSnapshot> preGatheringStorage;
    
    /**
     * Create a new tick context for processing a tribe's simulation tick.
     * 
     * @param tribe the tribe to process
     * @param familyService service for family operations
     * @param random random number generator
     */
    public TickContext(Tribe tribe, FamilyService familyService, Random random) {
        this.tribe = tribe;
        this.familyService = familyService;
        this.random = random;
        this.elderCount = 0;
        this.elderGatheringBonus = 1.0;
        this.preGatheringStorage = new HashMap<>();
    }
    
    /**
     * Capture current storage levels for a family before gathering.
     * 
     * @param family the family to snapshot
     */
    public void snapshotFamilyStorage(Family family) {
        preGatheringStorage.put(family, 
            new FamilyResourceSnapshot(
                family.getStorage().getFood(), 
                family.getStorage().getWater()
            )
        );
    }
    
    /**
     * Get the amount of food gathered by a family this tick.
     * 
     * @param family the family
     * @return food gathered this tick
     */
    public int getFoodGathered(Family family) {
        FamilyResourceSnapshot snapshot = preGatheringStorage.get(family);
        if (snapshot == null) return 0;
        return Math.max(0, family.getStorage().getFood() - snapshot.food);
    }
    
    /**
     * Get the amount of water gathered by a family this tick.
     * 
     * @param family the family
     * @return water gathered this tick
     */
    public int getWaterGathered(Family family) {
        FamilyResourceSnapshot snapshot = preGatheringStorage.get(family);
        if (snapshot == null) return 0;
        return Math.max(0, family.getStorage().getWater() - snapshot.water);
    }
    
    /**
     * Snapshot of family resource levels.
     */
    public record FamilyResourceSnapshot(int food, int water) {}
}
