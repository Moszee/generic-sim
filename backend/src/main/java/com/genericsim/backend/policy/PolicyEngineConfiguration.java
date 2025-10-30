package com.genericsim.backend.policy;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration for the policy engine.
 * Automatically discovers and registers all policy effects at application startup.
 */
@Configuration
public class PolicyEngineConfiguration {
    
    private final PolicyEngine policyEngine;
    private final List<PolicyEffect> policyEffects;
    
    /**
     * Spring automatically injects all PolicyEffect beans into the list.
     * 
     * @param policyEngine the policy engine
     * @param policyEffects all PolicyEffect beans discovered by Spring
     */
    public PolicyEngineConfiguration(
            PolicyEngine policyEngine,
            List<PolicyEffect> policyEffects) {
        this.policyEngine = policyEngine;
        this.policyEffects = policyEffects;
    }
    
    /**
     * Register all discovered policy effects with the engine after construction.
     * New effects added as @Component classes will be automatically registered.
     */
    @PostConstruct
    public void registerEffects() {
        for (PolicyEffect effect : policyEffects) {
            policyEngine.registerEffect(effect);
        }
    }
}
