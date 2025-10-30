package com.genericsim.backend.service;

import com.genericsim.backend.model.*;
import com.genericsim.backend.repository.LifestyleDefinitionRepository;
import com.genericsim.backend.repository.ResourceDefinitionRepository;
import com.genericsim.backend.repository.TechnologyDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DefinitionService initialization and retrieval
 */
@SpringBootTest
@Transactional
class DefinitionServiceTest {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private TechnologyDefinitionRepository technologyDefinitionRepository;

    @Autowired
    private LifestyleDefinitionRepository lifestyleDefinitionRepository;

    @Test
    void testResourceDefinitionsInitialized() {
        // Food should be initialized
        ResourceDefinition food = definitionService.getResourceDefinition(ResourceType.FOOD);
        assertNotNull(food);
        assertEquals(ResourceType.FOOD, food.getResourceType());
        assertEquals(3.0, food.getConsumptionRate());
        assertTrue(food.isRenewable());

        // Water should be initialized
        ResourceDefinition water = definitionService.getResourceDefinition(ResourceType.WATER);
        assertNotNull(water);
        assertEquals(ResourceType.WATER, water.getResourceType());
        assertEquals(4.0, water.getConsumptionRate());

        // Stone should be initialized
        ResourceDefinition stone = definitionService.getResourceDefinition(ResourceType.STONE);
        assertNotNull(stone);
        assertEquals(ResourceType.STONE, stone.getResourceType());
        assertEquals(0.0, stone.getConsumptionRate());
        assertEquals(0.0, stone.getDecayRate());

        // Wood should be initialized
        ResourceDefinition wood = definitionService.getResourceDefinition(ResourceType.WOOD);
        assertNotNull(wood);
        assertEquals(ResourceType.WOOD, wood.getResourceType());
    }

    @Test
    void testTechnologyDefinitionsInitialized() {
        // Fire should be initialized
        TechnologyDefinition fire = definitionService.getTechnologyDefinition(TechnologyType.FIRE);
        assertNotNull(fire);
        assertEquals(TechnologyType.FIRE, fire.getTechnologyType());
        assertEquals(50, fire.getResearchCost());
        assertTrue(fire.getEfficiencyBonus() > 0);

        // Stone Tools should be initialized with prerequisites
        TechnologyDefinition stoneTools = definitionService.getTechnologyDefinition(TechnologyType.STONE_TOOLS);
        assertNotNull(stoneTools);
        assertTrue(stoneTools.getPrerequisites().contains(TechnologyType.FIRE));
        assertTrue(stoneTools.getResourceCosts().containsKey(ResourceType.STONE));

        // Agriculture should be initialized
        TechnologyDefinition agriculture = definitionService.getTechnologyDefinition(TechnologyType.AGRICULTURE);
        assertNotNull(agriculture);
        assertTrue(agriculture.getPrerequisites().contains(TechnologyType.STONE_TOOLS));
    }

    @Test
    void testLifestyleDefinitionsInitialized() {
        // Hunter-Gatherer should be initialized
        LifestyleDefinition hunterGatherer = definitionService.getLifestyleDefinition(LifestyleType.HUNTER_GATHERER);
        assertNotNull(hunterGatherer);
        assertEquals(LifestyleType.HUNTER_GATHERER, hunterGatherer.getLifestyleType());
        assertEquals(1.0, hunterGatherer.getMobilityFactor());
        assertEquals(0.0, hunterGatherer.getMaintenanceCost());

        // Nomadic should be initialized
        LifestyleDefinition nomadic = definitionService.getLifestyleDefinition(LifestyleType.NOMADIC);
        assertNotNull(nomadic);
        assertTrue(nomadic.getRequiredTechnologies().contains(TechnologyType.ANIMAL_HUSBANDRY));

        // Settled should be initialized
        LifestyleDefinition settled = definitionService.getLifestyleDefinition(LifestyleType.SETTLED);
        assertNotNull(settled);
        assertTrue(settled.getRequiredTechnologies().contains(TechnologyType.AGRICULTURE));
        assertTrue(settled.getResourceGatheringModifiers().containsKey(ResourceType.FOOD));
    }

    @Test
    void testResourceDefinitionPersistence() {
        long countBefore = resourceDefinitionRepository.count();
        assertTrue(countBefore >= 4); // At least 4 resource types

        ResourceDefinition food = resourceDefinitionRepository.findByResourceType(ResourceType.FOOD).orElse(null);
        assertNotNull(food);
        assertNotNull(food.getId());
    }

    @Test
    void testTechnologyDefinitionPersistence() {
        long countBefore = technologyDefinitionRepository.count();
        assertTrue(countBefore >= 4); // At least 4 technology types

        TechnologyDefinition fire = technologyDefinitionRepository.findByTechnologyType(TechnologyType.FIRE).orElse(null);
        assertNotNull(fire);
        assertNotNull(fire.getId());
    }

    @Test
    void testLifestyleDefinitionPersistence() {
        long countBefore = lifestyleDefinitionRepository.count();
        assertTrue(countBefore >= 3); // At least 3 lifestyle types

        LifestyleDefinition hunterGatherer = lifestyleDefinitionRepository.findByLifestyleType(LifestyleType.HUNTER_GATHERER).orElse(null);
        assertNotNull(hunterGatherer);
        assertNotNull(hunterGatherer.getId());
    }
}
