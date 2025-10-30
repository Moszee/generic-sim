package com.genericsim.backend.policy;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central policy engine that manages and executes policy effects.
 * 
 * The engine maintains a registry of effects organized by phase and priority,
 * and executes them in the correct order during simulation ticks.
 * 
 * Effects are registered at startup and remain constant during execution.
 */
@Component
public class PolicyEngine {
    
    /**
     * Registry of effects organized by phase.
     * Within each phase, effects are sorted by priority (ascending).
     */
    private final Map<PolicyPhase, List<PolicyEffect>> effectsByPhase;
    
    /**
     * Create a new policy engine.
     * Effects are registered via registerEffect() or provided through dependency injection.
     */
    public PolicyEngine() {
        this.effectsByPhase = new EnumMap<>(PolicyPhase.class);
        for (PolicyPhase phase : PolicyPhase.values()) {
            effectsByPhase.put(phase, new ArrayList<>());
        }
    }
    
    /**
     * Register a policy effect with the engine.
     * Effects are automatically organized by phase and priority.
     * 
     * @param effect the effect to register
     */
    public void registerEffect(PolicyEffect effect) {
        List<PolicyEffect> phaseEffects = effectsByPhase.get(effect.getPhase());
        phaseEffects.add(effect);
        // Sort by priority after adding
        phaseEffects.sort(Comparator.comparingInt(PolicyEffect::getPriority));
    }
    
    /**
     * Execute all effects for a specific phase on the given context.
     * Effects are filtered using shouldApply() before execution.
     * 
     * @param phase the simulation phase to execute
     * @param context the tick context
     */
    public void executePhase(PolicyPhase phase, TickContext context) {
        List<PolicyEffect> effects = effectsByPhase.get(phase);
        for (PolicyEffect effect : effects) {
            if (effect.shouldApply(context)) {
                effect.apply(context);
            }
        }
    }
    
    /**
     * Get all registered effects for a specific phase.
     * Useful for debugging and testing.
     * 
     * @param phase the phase to query
     * @return immutable list of effects for that phase
     */
    public List<PolicyEffect> getEffectsForPhase(PolicyPhase phase) {
        return Collections.unmodifiableList(effectsByPhase.get(phase));
    }
    
    /**
     * Get count of registered effects across all phases.
     * 
     * @return total number of registered effects
     */
    public int getEffectCount() {
        return effectsByPhase.values().stream()
            .mapToInt(List::size)
            .sum();
    }
    
    /**
     * Get a summary of all registered effects by phase.
     * Useful for debugging and documentation.
     * 
     * @return map of phase to effect names
     */
    public Map<PolicyPhase, List<String>> getEffectSummary() {
        return effectsByPhase.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .map(PolicyEffect::getName)
                    .collect(Collectors.toList())
            ));
    }
}
