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
    private final FamilyService familyService;
    private final Random random = new Random();

    public TribeService(TribeRepository tribeRepository, FamilyService familyService) {
        this.tribeRepository = tribeRepository;
        this.familyService = familyService;
    }

    @Transactional
    public Tribe createTribe(String name, String description) {
        Tribe tribe = new Tribe(name, description);
        
        // Initialize resources (now mostly for backward compatibility)
        Resources resources = new Resources(100, 100);
        tribe.setResources(resources);
        
        // Initialize central storage if needed
        tribe.setCentralStorage(new Resources(0, 0));
        
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
        
        // Set initial skills based on role
        hunter1.setHuntingSkill(0.6);
        hunter2.setHuntingSkill(0.7);
        gatherer1.setGatheringSkill(0.6);
        gatherer2.setGatheringSkill(0.65);
        
        tribe.addMember(hunter1);
        tribe.addMember(hunter2);
        tribe.addMember(gatherer1);
        tribe.addMember(gatherer2);
        tribe.addMember(child);
        tribe.addMember(elder);
        
        // Initialize families
        familyService.initializeFamilies(tribe);
        
        return tribeRepository.save(tribe);
    }

    @Transactional
    public TribeStateDTO processTick(Long tribeId) {
        Tribe tribe = tribeRepository.findById(tribeId)
            .orElseThrow(() -> new RuntimeException("Tribe not found"));
        
        // Increment tick
        tribe.setCurrentTick(tribe.getCurrentTick() + 1);
        
        // Phase 1: Gathering - resources go to family storage
        for (Person person : tribe.getMembers()) {
            if (person.getHealth() <= 30) continue; // Too weak to work
            
            int foodGathered = 0;
            int waterGathered = 0;
            
            if (person.getRole() == Person.PersonRole.HUNTER) {
                // Hunting - skill-based food gathering
                int baseFood = 10 + random.nextInt(10);
                double skillMultiplier = 1.0 + person.getHuntingSkill();
                int incentive = tribe.getPolicy().getHuntingIncentive();
                foodGathered = (int) (baseFood * skillMultiplier) + incentive;
                
                // Improve hunting skill slightly on success
                if (foodGathered > 15) {
                    person.setHuntingSkill(Math.min(1.0, person.getHuntingSkill() + 0.01));
                }
            } else if (person.getRole() == Person.PersonRole.GATHERER) {
                // Gathering - skill-based food and water gathering
                int baseFood = 5 + random.nextInt(5);
                int baseWater = 8 + random.nextInt(8);
                double skillMultiplier = 1.0 + person.getGatheringSkill();
                int incentive = tribe.getPolicy().getGatheringIncentive();
                foodGathered = (int) (baseFood * skillMultiplier) + incentive;
                waterGathered = (int) (baseWater * skillMultiplier) + incentive;
                
                // Improve gathering skill slightly on success
                if (foodGathered > 7 || waterGathered > 10) {
                    person.setGatheringSkill(Math.min(1.0, person.getGatheringSkill() + 0.01));
                }
            }
            
            // Add gathered resources to family storage (or central if no family)
            if (person.getFamily() != null) {
                Resources familyStorage = person.getFamily().getStorage();
                
                // Apply central storage tax if enabled
                if (tribe.getPolicy().isEnableCentralStorage()) {
                    int taxRate = tribe.getPolicy().getCentralStorageTaxRate();
                    int foodTax = (foodGathered * taxRate) / 100;
                    int waterTax = (waterGathered * taxRate) / 100;
                    
                    tribe.getCentralStorage().setFood(tribe.getCentralStorage().getFood() + foodTax);
                    tribe.getCentralStorage().setWater(tribe.getCentralStorage().getWater() + waterTax);
                    
                    foodGathered -= foodTax;
                    waterGathered -= waterTax;
                }
                
                familyStorage.setFood(familyStorage.getFood() + foodGathered);
                familyStorage.setWater(familyStorage.getWater() + waterGathered);
            }
        }
        
        // Phase 2: Family upkeep and sharing
        for (Family family : tribe.getFamilies()) {
            boolean hasSufficient = familyService.consumeFamilyResources(family);
            
            if (!hasSufficient) {
                // Try to borrow from other families
                int foodNeeded = family.getMembers().size() * 3;
                int waterNeeded = family.getMembers().size() * 4;
                boolean borrowed = familyService.borrowResources(family, tribe, foodNeeded, waterNeeded);
                
                // If borrowing failed, try central storage
                if (!borrowed && tribe.getPolicy().isEnableCentralStorage()) {
                    borrowed = familyService.accessCentralStorage(family, tribe, foodNeeded, waterNeeded);
                }
                
                // If still insufficient, someone suffers
                if (!borrowed) {
                    Person toSuffer = familyService.selectMemberToSuffer(family, tribe.getPolicy());
                    if (toSuffer != null) {
                        toSuffer.setHealth(Math.max(0, toSuffer.getHealth() - 15));
                    }
                }
            } else {
                // Family has sufficient resources - members recover health slightly
                for (Person person : family.getMembers()) {
                    if (person.getHealth() < 100) {
                        person.setHealth(Math.min(100, person.getHealth() + 5));
                    }
                }
            }
        }
        
        // Phase 3: Storage decay (periodic)
        if (tribe.getCurrentTick() % tribe.getPolicy().getStorageDecayInterval() == 0) {
            familyService.applyStorageDecay(tribe, tribe.getPolicy().getStorageDecayRate());
        }
        
        // Phase 4: Aging and role transitions
        if (tribe.getCurrentTick() % 365 == 0) {
            for (Person person : tribe.getMembers()) {
                person.setAge(person.getAge() + 1);
                
                // Update roles based on age
                if (person.getAge() >= 60 && person.getRole() != Person.PersonRole.ELDER) {
                    person.setRole(Person.PersonRole.ELDER);
                } else if (person.getAge() >= 16 && person.getAge() < 60 && person.getRole() == Person.PersonRole.CHILD) {
                    // Assign role based on random or need
                    person.setRole(random.nextBoolean() ? Person.PersonRole.HUNTER : Person.PersonRole.GATHERER);
                    // Initialize appropriate skill
                    if (person.getRole() == Person.PersonRole.HUNTER) {
                        person.setHuntingSkill(0.5);
                    } else {
                        person.setGatheringSkill(0.5);
                    }
                }
            }
        }
        
        // Phase 5: Remove deceased members (health = 0)
        tribe.getMembers().removeIf(person -> person.getHealth() <= 0);
        
        // Update tribe resources for backward compatibility (sum of all family storage)
        int totalFood = tribe.getFamilies().stream()
            .mapToInt(f -> f.getStorage().getFood())
            .sum();
        int totalWater = tribe.getFamilies().stream()
            .mapToInt(f -> f.getStorage().getWater())
            .sum();
        tribe.getResources().setFood(totalFood);
        tribe.getResources().setWater(totalWater);
        
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
        if (policyUpdate.getSharingPriority() != null) {
            policy.setSharingPriority(Policy.SharingPriority.valueOf(policyUpdate.getSharingPriority()));
        }
        if (policyUpdate.getEnableCentralStorage() != null) {
            policy.setEnableCentralStorage(policyUpdate.getEnableCentralStorage());
        }
        if (policyUpdate.getCentralStorageTaxRate() != null) {
            policy.setCentralStorageTaxRate(policyUpdate.getCentralStorageTaxRate());
        }
        if (policyUpdate.getStorageDecayRate() != null) {
            policy.setStorageDecayRate(policyUpdate.getStorageDecayRate());
        }
        if (policyUpdate.getStorageDecayInterval() != null) {
            policy.setStorageDecayInterval(policyUpdate.getStorageDecayInterval());
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
        dto.setBondLevel(tribe.getBondLevel());
        dto.setResources(new TribeStateDTO.ResourcesDTO(tribe.getResources()));
        dto.setCentralStorage(new TribeStateDTO.ResourcesDTO(tribe.getCentralStorage()));
        dto.setPolicy(new TribeStateDTO.PolicyDTO(tribe.getPolicy()));
        dto.setMembers(tribe.getMembers().stream()
            .map(TribeStateDTO.PersonDTO::new)
            .collect(Collectors.toList()));
        dto.setFamilies(tribe.getFamilies().stream()
            .map(TribeStateDTO.FamilyDTO::new)
            .collect(Collectors.toList()));
        return dto;
    }
}
