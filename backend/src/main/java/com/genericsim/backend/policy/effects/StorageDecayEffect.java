package com.genericsim.backend.policy.effects;

import com.genericsim.backend.policy.PolicyEffect;
import com.genericsim.backend.policy.PolicyPhase;
import com.genericsim.backend.policy.TickContext;
import org.springframework.stereotype.Component;

/**
 * Policy effect that applies periodic decay to stored resources.
 * 
 * This simulates spoilage and degradation of food and water over time.
 * Decay is applied to both family storage and central storage at a
 * configurable rate and interval.
 * 
 * This executes during STORAGE_DECAY phase, after resource consumption
 * but before aging and progress calculation.
 */
@Component
public class StorageDecayEffect implements PolicyEffect {
    
    @Override
    public PolicyPhase getPhase() {
        return PolicyPhase.STORAGE_DECAY;
    }
    
    @Override
    public int getPriority() {
        return 100; // Core mechanic priority
    }
    
    @Override
    public boolean shouldApply(TickContext context) {
        var tribe = context.getTribe();
        var policy = tribe.getPolicy();
        
        // Only apply on configured intervals
        if (policy == null || policy.getStorageDecayInterval() <= 0) {
            return false;
        }
        
        return tribe.getCurrentTick() % policy.getStorageDecayInterval() == 0;
    }
    
    @Override
    public void apply(TickContext context) {
        var tribe = context.getTribe();
        double decayRate = tribe.getPolicy().getStorageDecayRate();
        
        // Use the existing family service method to apply decay
        context.getFamilyService().applyStorageDecay(tribe, decayRate);
    }
    
    @Override
    public String getName() {
        return "StorageDecay";
    }
}
