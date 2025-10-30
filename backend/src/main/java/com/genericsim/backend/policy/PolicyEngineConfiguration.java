package com.genericsim.backend.policy;

import com.genericsim.backend.policy.effects.CentralStorageTaxEffect;
import com.genericsim.backend.policy.effects.StorageDecayEffect;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration for the policy engine.
 * Registers all policy effects at application startup.
 */
@Configuration
public class PolicyEngineConfiguration {
    
    private final PolicyEngine policyEngine;
    private final CentralStorageTaxEffect centralStorageTaxEffect;
    private final StorageDecayEffect storageDecayEffect;
    
    public PolicyEngineConfiguration(
            PolicyEngine policyEngine,
            CentralStorageTaxEffect centralStorageTaxEffect,
            StorageDecayEffect storageDecayEffect) {
        this.policyEngine = policyEngine;
        this.centralStorageTaxEffect = centralStorageTaxEffect;
        this.storageDecayEffect = storageDecayEffect;
    }
    
    /**
     * Register all policy effects with the engine after construction.
     */
    @PostConstruct
    public void registerEffects() {
        policyEngine.registerEffect(centralStorageTaxEffect);
        policyEngine.registerEffect(storageDecayEffect);
    }
}
