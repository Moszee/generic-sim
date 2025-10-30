package com.genericsim.backend.policy;

import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.service.FamilyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PolicyEngineTest {

    private PolicyEngine policyEngine;

    @Mock
    private FamilyService familyService;

    @BeforeEach
    public void setUp() {
        policyEngine = new PolicyEngine();
    }

    @Test
    public void testRegisterEffect() {
        TestEffect effect = new TestEffect(PolicyPhase.GATHERING, 100);
        policyEngine.registerEffect(effect);

        assertEquals(1, policyEngine.getEffectCount());
        List<PolicyEffect> gatheringEffects = policyEngine.getEffectsForPhase(PolicyPhase.GATHERING);
        assertEquals(1, gatheringEffects.size());
        assertEquals(effect, gatheringEffects.get(0));
    }

    @Test
    public void testEffectPrioritySorting() {
        TestEffect effect1 = new TestEffect(PolicyPhase.GATHERING, 200);
        TestEffect effect2 = new TestEffect(PolicyPhase.GATHERING, 100);
        TestEffect effect3 = new TestEffect(PolicyPhase.GATHERING, 150);

        policyEngine.registerEffect(effect1);
        policyEngine.registerEffect(effect2);
        policyEngine.registerEffect(effect3);

        List<PolicyEffect> effects = policyEngine.getEffectsForPhase(PolicyPhase.GATHERING);
        assertEquals(3, effects.size());
        // Should be sorted by priority: 100, 150, 200
        assertEquals(100, effects.get(0).getPriority());
        assertEquals(150, effects.get(1).getPriority());
        assertEquals(200, effects.get(2).getPriority());
    }

    @Test
    public void testExecutePhaseWithFiltering() {
        TestEffect shouldApply = new TestEffect(PolicyPhase.GATHERING, 100, true);
        TestEffect shouldNotApply = new TestEffect(PolicyPhase.GATHERING, 200, false);

        policyEngine.registerEffect(shouldApply);
        policyEngine.registerEffect(shouldNotApply);

        Tribe tribe = new Tribe("Test", "Test");
        TickContext context = new TickContext(tribe, familyService, new Random());

        policyEngine.executePhase(PolicyPhase.GATHERING, context);

        assertTrue(shouldApply.wasApplied);
        assertFalse(shouldNotApply.wasApplied);
    }

    @Test
    public void testMultiplePhases() {
        TestEffect gatheringEffect = new TestEffect(PolicyPhase.GATHERING, 100);
        TestEffect decayEffect = new TestEffect(PolicyPhase.STORAGE_DECAY, 100);

        policyEngine.registerEffect(gatheringEffect);
        policyEngine.registerEffect(decayEffect);

        assertEquals(2, policyEngine.getEffectCount());
        assertEquals(1, policyEngine.getEffectsForPhase(PolicyPhase.GATHERING).size());
        assertEquals(1, policyEngine.getEffectsForPhase(PolicyPhase.STORAGE_DECAY).size());
        assertEquals(0, policyEngine.getEffectsForPhase(PolicyPhase.AGING).size());
    }

    @Test
    public void testGetEffectSummary() {
        TestEffect effect1 = new TestEffect(PolicyPhase.GATHERING, 100);
        TestEffect effect2 = new TestEffect(PolicyPhase.STORAGE_DECAY, 100);

        policyEngine.registerEffect(effect1);
        policyEngine.registerEffect(effect2);

        var summary = policyEngine.getEffectSummary();
        assertTrue(summary.containsKey(PolicyPhase.GATHERING));
        assertTrue(summary.containsKey(PolicyPhase.STORAGE_DECAY));
        assertTrue(summary.get(PolicyPhase.GATHERING).contains("TestEffect"));
    }

    // Test implementation of PolicyEffect
    private static class TestEffect implements PolicyEffect {
        private final PolicyPhase phase;
        private final int priority;
        private final boolean shouldApply;
        private boolean wasApplied = false;

        public TestEffect(PolicyPhase phase, int priority) {
            this(phase, priority, true);
        }

        public TestEffect(PolicyPhase phase, int priority, boolean shouldApply) {
            this.phase = phase;
            this.priority = priority;
            this.shouldApply = shouldApply;
        }

        @Override
        public PolicyPhase getPhase() {
            return phase;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public boolean shouldApply(TickContext context) {
            return shouldApply;
        }

        @Override
        public void apply(TickContext context) {
            wasApplied = true;
        }

        @Override
        public String getName() {
            return "TestEffect";
        }
    }
}
