package com.genericsim.backend.policy.effects;

import com.genericsim.backend.model.Family;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Resources;
import com.genericsim.backend.policy.PolicyEffect;
import com.genericsim.backend.policy.PolicyPhase;
import com.genericsim.backend.policy.TickContext;
import org.springframework.stereotype.Component;

/**
 * Policy effect that applies taxation on gathered resources for central storage.
 * 
 * When central storage is enabled, this effect taxes a percentage of all
 * resources gathered by tribe members and deposits them into the central pool.
 * 
 * This executes during PRODUCTION phase, after resources have been collected
 * but before upkeep consumption.
 */
@Component
public class CentralStorageTaxEffect implements PolicyEffect {
    
    @Override
    public PolicyPhase getPhase() {
        return PolicyPhase.PRODUCTION;
    }
    
    @Override
    public int getPriority() {
        return 100; // Core mechanic priority
    }
    
    @Override
    public boolean shouldApply(TickContext context) {
        // Only apply if central storage is enabled
        return context.getTribe().getPolicy() != null 
            && context.getTribe().getPolicy().isEnableCentralStorage();
    }
    
    @Override
    public void apply(TickContext context) {
        var tribe = context.getTribe();
        var policy = tribe.getPolicy();
        int taxRate = policy.getCentralStorageTaxRate();
        Resources centralStorage = tribe.getCentralStorage();
        
        // Process tax for each family based on what they gathered this tick
        for (Family family : tribe.getFamilies()) {
            int foodGathered = context.getFoodGathered(family);
            int waterGathered = context.getWaterGathered(family);
            
            // Calculate tax on gathered resources
            int foodTax = (foodGathered * taxRate) / 100;
            int waterTax = (waterGathered * taxRate) / 100;
            
            // Transfer tax to central storage
            if (foodTax > 0) {
                Resources familyStorage = family.getStorage();
                familyStorage.setFood(familyStorage.getFood() - foodTax);
                centralStorage.setFood(centralStorage.getFood() + foodTax);
            }
            
            if (waterTax > 0) {
                Resources familyStorage = family.getStorage();
                familyStorage.setWater(familyStorage.getWater() - waterTax);
                centralStorage.setWater(centralStorage.getWater() + waterTax);
            }
        }
    }
    
    @Override
    public String getName() {
        return "CentralStorageTax";
    }
}
