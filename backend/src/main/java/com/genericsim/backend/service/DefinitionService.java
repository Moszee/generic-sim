package com.genericsim.backend.service;

import com.genericsim.backend.model.*;
import com.genericsim.backend.repository.LifestyleDefinitionRepository;
import com.genericsim.backend.repository.ResourceDefinitionRepository;
import com.genericsim.backend.repository.TechnologyDefinitionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing resource, technology, and lifestyle definitions.
 * Initializes default configurations on startup.
 */
@Service
public class DefinitionService {

    private final ResourceDefinitionRepository resourceDefinitionRepository;
    private final TechnologyDefinitionRepository technologyDefinitionRepository;
    private final LifestyleDefinitionRepository lifestyleDefinitionRepository;

    public DefinitionService(ResourceDefinitionRepository resourceDefinitionRepository,
                           TechnologyDefinitionRepository technologyDefinitionRepository,
                           LifestyleDefinitionRepository lifestyleDefinitionRepository) {
        this.resourceDefinitionRepository = resourceDefinitionRepository;
        this.technologyDefinitionRepository = technologyDefinitionRepository;
        this.lifestyleDefinitionRepository = lifestyleDefinitionRepository;
    }

    /**
     * Initialize default definitions on application startup
     */
    @PostConstruct
    @Transactional
    public void initializeDefaults() {
        initializeResourceDefinitions();
        initializeTechnologyDefinitions();
        initializeLifestyleDefinitions();
    }

    private void initializeResourceDefinitions() {
        // Food - basic sustenance
        if (resourceDefinitionRepository.findByResourceType(ResourceType.FOOD).isEmpty()) {
            ResourceDefinition food = new ResourceDefinition(ResourceType.FOOD, 1.0, 3.0);
            food.setDecayRate(0.1);
            food.setStorageCapacity(1000);
            resourceDefinitionRepository.save(food);
        }

        // Water - essential liquid
        if (resourceDefinitionRepository.findByResourceType(ResourceType.WATER).isEmpty()) {
            ResourceDefinition water = new ResourceDefinition(ResourceType.WATER, 1.0, 4.0);
            water.setDecayRate(0.05);
            water.setStorageCapacity(1000);
            resourceDefinitionRepository.save(water);
        }

        // Stone - basic material, no consumption
        if (resourceDefinitionRepository.findByResourceType(ResourceType.STONE).isEmpty()) {
            ResourceDefinition stone = new ResourceDefinition(ResourceType.STONE, 0.5, 0.0);
            stone.setDecayRate(0.0);
            stone.setStorageCapacity(500);
            stone.setWeight(2.0);
            resourceDefinitionRepository.save(stone);
        }

        // Wood - basic construction material, no consumption
        if (resourceDefinitionRepository.findByResourceType(ResourceType.WOOD).isEmpty()) {
            ResourceDefinition wood = new ResourceDefinition(ResourceType.WOOD, 0.8, 0.0);
            wood.setDecayRate(0.02);
            wood.setStorageCapacity(500);
            wood.setWeight(1.5);
            resourceDefinitionRepository.save(wood);
        }
    }

    private void initializeTechnologyDefinitions() {
        // Fire - basic technology
        if (technologyDefinitionRepository.findByTechnologyType(TechnologyType.FIRE).isEmpty()) {
            TechnologyDefinition fire = new TechnologyDefinition(TechnologyType.FIRE, 50);
            fire.setEfficiencyBonus(0.1);
            fire.setStabilityBonus(5.0);
            technologyDefinitionRepository.save(fire);
        }

        // Stone Tools - requires fire
        if (technologyDefinitionRepository.findByTechnologyType(TechnologyType.STONE_TOOLS).isEmpty()) {
            TechnologyDefinition stoneTools = new TechnologyDefinition(TechnologyType.STONE_TOOLS, 100);
            stoneTools.setEfficiencyBonus(0.15);
            stoneTools.getPrerequisites().add(TechnologyType.FIRE);
            stoneTools.getResourceCosts().put(ResourceType.STONE, 20);
            technologyDefinitionRepository.save(stoneTools);
        }

        // Agriculture - requires stone tools
        if (technologyDefinitionRepository.findByTechnologyType(TechnologyType.AGRICULTURE).isEmpty()) {
            TechnologyDefinition agriculture = new TechnologyDefinition(TechnologyType.AGRICULTURE, 200);
            agriculture.setEfficiencyBonus(0.3);
            agriculture.setStabilityBonus(10.0);
            agriculture.getPrerequisites().add(TechnologyType.STONE_TOOLS);
            technologyDefinitionRepository.save(agriculture);
        }

        // Animal Husbandry
        if (technologyDefinitionRepository.findByTechnologyType(TechnologyType.ANIMAL_HUSBANDRY).isEmpty()) {
            TechnologyDefinition animalHusbandry = new TechnologyDefinition(TechnologyType.ANIMAL_HUSBANDRY, 150);
            animalHusbandry.setEfficiencyBonus(0.2);
            animalHusbandry.setStabilityBonus(8.0);
            technologyDefinitionRepository.save(animalHusbandry);
        }
    }

    private void initializeLifestyleDefinitions() {
        // Hunter-Gatherer - default lifestyle
        if (lifestyleDefinitionRepository.findByLifestyleType(LifestyleType.HUNTER_GATHERER).isEmpty()) {
            LifestyleDefinition hunterGatherer = new LifestyleDefinition(LifestyleType.HUNTER_GATHERER, 1.0, 0.8);
            hunterGatherer.setMaintenanceCost(0.0);
            hunterGatherer.getResourceGatheringModifiers().put(ResourceType.FOOD, 1.0);
            hunterGatherer.getResourceGatheringModifiers().put(ResourceType.WATER, 1.0);
            lifestyleDefinitionRepository.save(hunterGatherer);
        }

        // Nomadic - requires animal husbandry
        if (lifestyleDefinitionRepository.findByLifestyleType(LifestyleType.NOMADIC).isEmpty()) {
            LifestyleDefinition nomadic = new LifestyleDefinition(LifestyleType.NOMADIC, 0.9, 0.9);
            nomadic.setMaintenanceCost(5.0);
            nomadic.getResourceGatheringModifiers().put(ResourceType.FOOD, 1.2);
            nomadic.getRequiredTechnologies().add(TechnologyType.ANIMAL_HUSBANDRY);
            lifestyleDefinitionRepository.save(nomadic);
        }

        // Settled - requires agriculture
        if (lifestyleDefinitionRepository.findByLifestyleType(LifestyleType.SETTLED).isEmpty()) {
            LifestyleDefinition settled = new LifestyleDefinition(LifestyleType.SETTLED, 0.2, 1.2);
            settled.setMaintenanceCost(10.0);
            settled.getResourceGatheringModifiers().put(ResourceType.FOOD, 1.5);
            settled.getResourceGatheringModifiers().put(ResourceType.WOOD, 1.3);
            settled.getResourceGatheringModifiers().put(ResourceType.STONE, 1.3);
            settled.getRequiredTechnologies().add(TechnologyType.AGRICULTURE);
            lifestyleDefinitionRepository.save(settled);
        }
    }

    public ResourceDefinition getResourceDefinition(ResourceType type) {
        return resourceDefinitionRepository.findByResourceType(type)
                .orElseGet(() -> {
                    ResourceDefinition def = new ResourceDefinition(type);
                    return resourceDefinitionRepository.save(def);
                });
    }

    public TechnologyDefinition getTechnologyDefinition(TechnologyType type) {
        return technologyDefinitionRepository.findByTechnologyType(type)
                .orElseGet(() -> {
                    TechnologyDefinition def = new TechnologyDefinition(type);
                    return technologyDefinitionRepository.save(def);
                });
    }

    public LifestyleDefinition getLifestyleDefinition(LifestyleType type) {
        return lifestyleDefinitionRepository.findByLifestyleType(type)
                .orElseGet(() -> {
                    LifestyleDefinition def = new LifestyleDefinition(type);
                    return lifestyleDefinitionRepository.save(def);
                });
    }
}
