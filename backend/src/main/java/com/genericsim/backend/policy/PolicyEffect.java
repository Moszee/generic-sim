package com.genericsim.backend.policy;

/**
 * Base interface for all policy effects in the simulation.
 * 
 * Policy effects are self-contained units of logic that execute during specific
 * simulation phases. Each effect can inspect the tick context and modify the
 * simulation state as needed.
 * 
 * Effects are executed in priority order within their registered phase.
 * Lower priority values execute first (e.g., priority 10 before priority 100).
 */
public interface PolicyEffect {
    
    /**
     * Get the simulation phase where this effect should execute.
     * 
     * @return the phase for this effect
     */
    PolicyPhase getPhase();
    
    /**
     * Get the execution priority within the phase.
     * Effects with lower priority values execute first.
     * 
     * Recommended priority ranges:
     * - 0-99: Pre-processing and setup effects
     * - 100-199: Core game mechanics
     * - 200-299: Post-processing and adjustments
     * - 300+: Final cleanup and validation
     * 
     * @return the priority (lower executes first)
     */
    int getPriority();
    
    /**
     * Check if this effect should execute for the given context.
     * This is called before apply() and allows for efficient filtering.
     * 
     * For example, storage decay might only apply every N ticks.
     * 
     * @param context the current tick context
     * @return true if this effect should execute, false to skip
     */
    boolean shouldApply(TickContext context);
    
    /**
     * Apply this effect to the simulation state.
     * 
     * Effects can read from and write to the context to modify simulation state.
     * This method is only called if shouldApply() returns true.
     * 
     * @param context the current tick context (mutable)
     */
    void apply(TickContext context);
    
    /**
     * Get a human-readable name for this effect.
     * Used for logging and debugging.
     * 
     * @return the effect name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
