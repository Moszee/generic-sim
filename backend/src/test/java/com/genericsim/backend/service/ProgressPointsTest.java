package com.genericsim.backend.service;

import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.dto.TribeStatisticsDTO;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.repository.TribeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProgressPointsTest {

    @Autowired
    private TribeService tribeService;

    @Autowired
    private TribeRepository tribeRepository;

    @Test
    public void testAgeGroupCategorization() {
        // Test all age group boundaries
        assertEquals(Person.AgeGroup.CHILD, Person.AgeGroup.fromAge(0));
        assertEquals(Person.AgeGroup.CHILD, Person.AgeGroup.fromAge(8));
        assertEquals(Person.AgeGroup.CHILD, Person.AgeGroup.fromAge(15));
        
        assertEquals(Person.AgeGroup.YOUNG_ADULT, Person.AgeGroup.fromAge(16));
        assertEquals(Person.AgeGroup.YOUNG_ADULT, Person.AgeGroup.fromAge(25));
        assertEquals(Person.AgeGroup.YOUNG_ADULT, Person.AgeGroup.fromAge(40));
        
        assertEquals(Person.AgeGroup.ADULT, Person.AgeGroup.fromAge(41));
        assertEquals(Person.AgeGroup.ADULT, Person.AgeGroup.fromAge(50));
        assertEquals(Person.AgeGroup.ADULT, Person.AgeGroup.fromAge(59));
        
        assertEquals(Person.AgeGroup.ELDER, Person.AgeGroup.fromAge(60));
        assertEquals(Person.AgeGroup.ELDER, Person.AgeGroup.fromAge(70));
        assertEquals(Person.AgeGroup.ELDER, Person.AgeGroup.fromAge(100));
    }

    @Test
    public void testPersonAgeGroupMethod() {
        Person child = new Person("Child", Person.PersonRole.CHILD, 10, 100);
        assertEquals(Person.AgeGroup.CHILD, child.getAgeGroup());
        
        Person youngAdult = new Person("Young", Person.PersonRole.HUNTER, 25, 100);
        assertEquals(Person.AgeGroup.YOUNG_ADULT, youngAdult.getAgeGroup());
        
        Person adult = new Person("Adult", Person.PersonRole.HUNTER, 45, 100);
        assertEquals(Person.AgeGroup.ADULT, adult.getAgeGroup());
        
        Person elder = new Person("Elder", Person.PersonRole.ELDER, 65, 80);
        assertEquals(Person.AgeGroup.ELDER, elder.getAgeGroup());
    }

    @Test
    public void testInitialProgressPoints() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        // Initial progress points should be 0
        assertEquals(0, tribe.getProgressPoints());
        
        TribeStateDTO state = tribeService.getTribeState(tribe.getId());
        assertEquals(0, state.getProgressPoints());
    }

    @Test
    public void testProgressPointsAfterTick() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Initial state
        TribeStateDTO initialState = tribeService.getTribeState(tribeId);
        assertEquals(0, initialState.getProgressPoints());
        
        // Process one tick
        TribeStateDTO newState = tribeService.processTick(tribeId);
        
        // Progress should have changed (could be positive or negative depending on demographics)
        // Just verify it's been calculated
        assertNotNull(newState.getProgressPoints());
    }

    @Test
    public void testProgressGenerationByAgeGroup() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Get statistics to see age breakdown
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Verify age group breakdown exists
        assertNotNull(stats.getAgeGroupBreakdown());
        assertNotNull(stats.getProgressStats());
        
        // Progress generation should be: youngAdults * 2 + adults * 1
        int expectedGeneration = (stats.getAgeGroupBreakdown().getYoungAdults() * 2) 
                               + (stats.getAgeGroupBreakdown().getAdults() * 1);
        assertEquals(expectedGeneration, stats.getProgressStats().getProgressGeneration());
    }

    @Test
    public void testElderPreservationBonus() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Elder preservation should be: elderCount * 10
        int expectedPreservation = stats.getAgeGroupBreakdown().getElders() * 10;
        assertEquals(expectedPreservation, stats.getProgressStats().getElderPreservation());
    }

    @Test
    public void testProgressDecayCalculation() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Base decay is 30, reduced by elder preservation
        int baseDecay = 30;
        int elderPreservation = stats.getAgeGroupBreakdown().getElders() * 10;
        int expectedDecay = Math.max(0, baseDecay - elderPreservation);
        
        assertEquals(expectedDecay, stats.getProgressStats().getProgressDecay());
    }

    @Test
    public void testNetProgressPerTick() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Net progress = generation - decay
        int expectedNet = stats.getProgressStats().getProgressGeneration() 
                        - stats.getProgressStats().getProgressDecay();
        assertEquals(expectedNet, stats.getProgressStats().getNetProgressPerTick());
    }

    @Test
    public void testElderGatheringBonus() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Elder gathering bonus should be 2% per elder
        double expectedBonus = stats.getAgeGroupBreakdown().getElders() * 2.0;
        assertEquals(expectedBonus, stats.getProgressStats().getElderGatheringBonus(), 0.01);
    }

    @Test
    public void testProgressPointsNeverNegative() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Process multiple ticks
        for (int i = 0; i < 10; i++) {
            tribeService.processTick(tribeId);
        }
        
        TribeStateDTO state = tribeService.getTribeState(tribeId);
        
        // Progress points should never be negative
        assertTrue(state.getProgressPoints() >= 0);
    }

    @Test
    public void testProgressPointsAccumulation() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Clear existing members and add a balanced demographic
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // Add members to create positive progress
        // 4 young adults (16-40): +8 progress
        // 2 adults (41-59): +2 progress
        // 3 elders (60+): preserve 30 points (cancels the -30 decay)
        tribe.addMember(new Person("Young1", Person.PersonRole.HUNTER, 25, 100));
        tribe.addMember(new Person("Young2", Person.PersonRole.GATHERER, 30, 100));
        tribe.addMember(new Person("Young3", Person.PersonRole.HUNTER, 35, 100));
        tribe.addMember(new Person("Young4", Person.PersonRole.GATHERER, 20, 100));
        tribe.addMember(new Person("Adult1", Person.PersonRole.HUNTER, 45, 100));
        tribe.addMember(new Person("Adult2", Person.PersonRole.GATHERER, 50, 100));
        tribe.addMember(new Person("Elder1", Person.PersonRole.ELDER, 65, 80));
        tribe.addMember(new Person("Elder2", Person.PersonRole.ELDER, 70, 80));
        tribe.addMember(new Person("Elder3", Person.PersonRole.ELDER, 75, 80));
        
        tribeRepository.save(tribe);
        
        // Get initial progress
        int initialProgress = tribe.getProgressPoints();
        
        // Process one tick
        tribeService.processTick(tribeId);
        
        TribeStateDTO state = tribeService.getTribeState(tribeId);
        
        // With 4 young adults (+8), 2 adults (+2), 3 elders (preserve 30)
        // Progress = +10, Decay = max(0, 30 - 30) = 0
        // Net = +10 per tick
        assertEquals(initialProgress + 10, state.getProgressPoints());
    }

    @Test
    public void testNoElderScenario() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Clear existing members and add only young workers (no elders)
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // Add only young adults and adults (no elders)
        tribe.addMember(new Person("Young1", Person.PersonRole.HUNTER, 25, 100));
        tribe.addMember(new Person("Young2", Person.PersonRole.GATHERER, 30, 100));
        tribe.addMember(new Person("Adult1", Person.PersonRole.HUNTER, 45, 100));
        
        tribeRepository.save(tribe);
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // With 0 elders, preservation should be 0
        assertEquals(0, stats.getProgressStats().getElderPreservation());
        // Decay should be full 30 points
        assertEquals(30, stats.getProgressStats().getProgressDecay());
        
        // Generation: 2 young adults * 2 + 1 adult * 1 = 5
        // Net: 5 - 30 = -25 (but will be capped at 0 for actual progress)
        assertEquals(-25, stats.getProgressStats().getNetProgressPerTick());
    }

    @Test
    public void testElderHeavyScenario() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        // Clear existing members and add many elders
        tribe = tribeRepository.findById(tribeId).orElseThrow();
        tribe.getMembers().clear();
        
        // Add 5 elders (preserve 50 points, exceeds decay)
        for (int i = 0; i < 5; i++) {
            tribe.addMember(new Person("Elder" + i, Person.PersonRole.ELDER, 65 + i, 80));
        }
        // Add 2 young adults for some progress generation
        tribe.addMember(new Person("Young1", Person.PersonRole.HUNTER, 25, 100));
        tribe.addMember(new Person("Young2", Person.PersonRole.GATHERER, 30, 100));
        
        tribeRepository.save(tribe);
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // With 5 elders, preservation should be 50
        assertEquals(50, stats.getProgressStats().getElderPreservation());
        // Decay should be 0 (30 - 50 capped at 0)
        assertEquals(0, stats.getProgressStats().getProgressDecay());
        
        // Generation: 2 young adults * 2 = 4
        // Net: 4 - 0 = 4
        assertEquals(4, stats.getProgressStats().getNetProgressPerTick());
    }

    @Test
    public void testAgeGroupBreakdownInStatistics() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStatisticsDTO stats = tribeService.getTribeStatistics(tribeId);
        
        // Verify age breakdown is populated
        assertNotNull(stats.getAgeGroupBreakdown());
        
        // The default tribe has: 2 hunters (25, 28), 2 gatherers (24, 26), 1 child (8), 1 elder (65)
        // Age groups: 1 child, 4 young adults, 0 adults, 1 elder
        assertEquals(1, stats.getAgeGroupBreakdown().getChildren());
        assertEquals(4, stats.getAgeGroupBreakdown().getYoungAdults());
        assertEquals(0, stats.getAgeGroupBreakdown().getAdults());
        assertEquals(1, stats.getAgeGroupBreakdown().getElders());
    }
}
