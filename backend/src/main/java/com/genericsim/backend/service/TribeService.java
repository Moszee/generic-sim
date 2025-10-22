package com.genericsim.backend.service;

import com.genericsim.backend.dto.PolicyUpdateDTO;
import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.dto.TribeStatisticsDTO;
import com.genericsim.backend.model.*;
import com.genericsim.backend.repository.TribeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TribeService {

    private final TribeRepository tribeRepository;
    private final Random random = new Random();

    public TribeService(TribeRepository tribeRepository) {
        this.tribeRepository = tribeRepository;
    }

    @Transactional
    public Tribe createTribe(String name, String description) {
        Tribe tribe = new Tribe(name, description);
        
        // Initialize resources
        Resources resources = new Resources(100, 100);
        tribe.setResources(resources);
        
        // Initialize policy with default values
        Policy policy = new Policy("Default Policy", "Standard tribe policy", 10, 10, 5, 5);
        tribe.setPolicy(policy);
        
        // Initialize with some starting members
        Person hunter1 = new Person("Hunter Alpha", Person.PersonRole.HUNTER, 25, 100);
        Person hunter2 = new Person("Hunter Beta", Person.PersonRole.HUNTER, 28, 100);
        Person gatherer1 = new Person("Gatherer Alpha", Person.PersonRole.GATHERER, 24, 100);
        Person gatherer2 = new Person("Gatherer Beta", Person.PersonRole.GATHERER, 26, 100);
        Person child = new Person("Child Alpha", Person.PersonRole.CHILD, 8, 100);
        Person elder = new Person("Elder Wise", Person.PersonRole.ELDER, 65, 80);
        
        tribe.addMember(hunter1);
        tribe.addMember(hunter2);
        tribe.addMember(gatherer1);
        tribe.addMember(gatherer2);
        tribe.addMember(child);
        tribe.addMember(elder);
        
        return tribeRepository.save(tribe);
    }

    @Transactional
    public TribeStateDTO processTick(Long tribeId) {
        Tribe tribe = tribeRepository.findById(tribeId)
            .orElseThrow(() -> new RuntimeException("Tribe not found"));
        
        // Increment tick
        tribe.setCurrentTick(tribe.getCurrentTick() + 1);
        
        // Hunters gather food
        int foodGathered = 0;
        int waterGathered = 0;
        
        for (Person person : tribe.getMembers()) {
            if (person.getRole() == Person.PersonRole.HUNTER && person.getHealth() > 30) {
                int baseFood = 10 + random.nextInt(10);
                int incentive = tribe.getPolicy().getHuntingIncentive();
                foodGathered += baseFood + incentive;
            } else if (person.getRole() == Person.PersonRole.GATHERER && person.getHealth() > 30) {
                int baseFood = 5 + random.nextInt(5);
                int baseWater = 8 + random.nextInt(8);
                int incentive = tribe.getPolicy().getGatheringIncentive();
                foodGathered += baseFood + incentive;
                waterGathered += baseWater + incentive;
            }
        }
        
        // Update resources with gathered amounts
        Resources resources = tribe.getResources();
        resources.setFood(resources.getFood() + foodGathered);
        resources.setWater(resources.getWater() + waterGathered);
        
        // Apply taxes
        int foodTax = (foodGathered * tribe.getPolicy().getFoodTaxRate()) / 100;
        int waterTax = (waterGathered * tribe.getPolicy().getWaterTaxRate()) / 100;
        resources.setFood(resources.getFood() - foodTax);
        resources.setWater(resources.getWater() - waterTax);
        
        // Consume resources (each person needs food and water)
        int totalMembers = tribe.getMembers().size();
        int foodConsumed = totalMembers * 3;
        int waterConsumed = totalMembers * 4;
        
        resources.setFood(Math.max(0, resources.getFood() - foodConsumed));
        resources.setWater(Math.max(0, resources.getWater() - waterConsumed));
        
        // Update person health based on resource availability
        for (Person person : tribe.getMembers()) {
            if (resources.getFood() < 10 || resources.getWater() < 10) {
                // Low resources - health decreases
                person.setHealth(Math.max(0, person.getHealth() - 10));
            } else if (person.getHealth() < 100) {
                // Good resources - health recovers
                person.setHealth(Math.min(100, person.getHealth() + 5));
            }
            
            // Age people
            if (tribe.getCurrentTick() % 365 == 0) {
                person.setAge(person.getAge() + 1);
                
                // Update roles based on age
                if (person.getAge() >= 60 && person.getRole() != Person.PersonRole.ELDER) {
                    person.setRole(Person.PersonRole.ELDER);
                } else if (person.getAge() >= 16 && person.getAge() < 60 && person.getRole() == Person.PersonRole.CHILD) {
                    // Assign role based on random or need
                    person.setRole(random.nextBoolean() ? Person.PersonRole.HUNTER : Person.PersonRole.GATHERER);
                }
            }
        }
        
        // Remove deceased members (health = 0)
        tribe.getMembers().removeIf(person -> person.getHealth() <= 0);
        
        tribeRepository.save(tribe);
        
        return convertToDTO(tribe);
    }

    public TribeStateDTO getTribeState(Long tribeId) {
        Tribe tribe = tribeRepository.findById(tribeId)
            .orElseThrow(() -> new RuntimeException("Tribe not found"));
        return convertToDTO(tribe);
    }

    public List<TribeStateDTO> getAllTribes() {
        return tribeRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get tribe statistics in a frontend-friendly format.
     * Provides aggregated data including population counts, role breakdown,
     * health statistics, and resource status.
     * 
     * @param tribeId the ID of the tribe
     * @return TribeStatisticsDTO containing aggregated statistics
     * @throws RuntimeException if tribe is not found
     */
    public TribeStatisticsDTO getTribeStatistics(Long tribeId) {
        Tribe tribe = tribeRepository.findById(tribeId)
            .orElseThrow(() -> new RuntimeException("Tribe not found"));
        return new TribeStatisticsDTO(tribe);
    }

    /**
     * Update the policy settings for a tribe.
     * Only non-null values in the PolicyUpdateDTO will be applied.
     * 
     * @param tribeId the ID of the tribe
     * @param policyUpdate the policy changes to apply
     * @return TribeStateDTO with updated state
     * @throws RuntimeException if tribe is not found
     */
    @Transactional
    public TribeStateDTO updateTribePolicy(Long tribeId, PolicyUpdateDTO policyUpdate) {
        Tribe tribe = tribeRepository.findById(tribeId)
            .orElseThrow(() -> new RuntimeException("Tribe not found"));
        
        Policy policy = tribe.getPolicy();
        if (policy == null) {
            policy = new Policy("Default Policy", "Standard tribe policy", 10, 10, 5, 5);
            tribe.setPolicy(policy);
        }
        
        // Update only non-null values
        if (policyUpdate.getFoodTaxRate() != null) {
            policy.setFoodTaxRate(policyUpdate.getFoodTaxRate());
        }
        if (policyUpdate.getWaterTaxRate() != null) {
            policy.setWaterTaxRate(policyUpdate.getWaterTaxRate());
        }
        if (policyUpdate.getHuntingIncentive() != null) {
            policy.setHuntingIncentive(policyUpdate.getHuntingIncentive());
        }
        if (policyUpdate.getGatheringIncentive() != null) {
            policy.setGatheringIncentive(policyUpdate.getGatheringIncentive());
        }
        
        tribeRepository.save(tribe);
        return convertToDTO(tribe);
    }

    private TribeStateDTO convertToDTO(Tribe tribe) {
        TribeStateDTO dto = new TribeStateDTO();
        dto.setTribeId(tribe.getId());
        dto.setTribeName(tribe.getName());
        dto.setDescription(tribe.getDescription());
        dto.setCurrentTick(tribe.getCurrentTick());
        dto.setResources(new TribeStateDTO.ResourcesDTO(tribe.getResources()));
        dto.setPolicy(new TribeStateDTO.PolicyDTO(tribe.getPolicy()));
        dto.setMembers(tribe.getMembers().stream()
            .map(TribeStateDTO.PersonDTO::new)
            .collect(Collectors.toList()));
        return dto;
    }
}
