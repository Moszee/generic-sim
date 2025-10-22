package com.genericsim.backend.service;

import com.genericsim.backend.model.*;
import com.genericsim.backend.repository.FamilyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final Random random = new Random();

    public FamilyService(FamilyRepository familyRepository) {
        this.familyRepository = familyRepository;
    }

    /**
     * Create families and distribute tribe members among them.
     * Generally creates 2-3 families per tribe.
     */
    @Transactional
    public void initializeFamilies(Tribe tribe) {
        int memberCount = tribe.getMembers().size();
        int familyCount = Math.max(1, memberCount / 3); // Roughly 3 people per family
        
        // Create families
        List<Family> families = new ArrayList<>();
        for (int i = 0; i < familyCount; i++) {
            Family family = new Family("Family " + (char)('A' + i));
            family.setTribe(tribe);
            family.setStorage(new Resources(30, 30)); // Starting storage
            families.add(family);
        }
        
        // Distribute members to families
        List<Person> members = new ArrayList<>(tribe.getMembers());
        Collections.shuffle(members);
        
        for (int i = 0; i < members.size(); i++) {
            Person person = members.get(i);
            Family family = families.get(i % familyCount);
            family.addMember(person);
        }
        
        // Save families
        for (Family family : families) {
            tribe.addFamily(family);
        }
    }

    /**
     * Process daily resource consumption for a family.
     * Each member needs 3 food and 4 water per day.
     * Returns true if family had sufficient resources.
     */
    public boolean consumeFamilyResources(Family family) {
        int memberCount = family.getMembers().size();
        int foodNeeded = memberCount * 3;
        int waterNeeded = memberCount * 4;
        
        Resources storage = family.getStorage();
        boolean hasSufficient = storage.getFood() >= foodNeeded && storage.getWater() >= waterNeeded;
        
        storage.setFood(Math.max(0, storage.getFood() - foodNeeded));
        storage.setWater(Math.max(0, storage.getWater() - waterNeeded));
        
        return hasSufficient;
    }

    /**
     * Attempt to borrow resources from other families.
     * Borrowing from richest first, success depends on tribe bond level.
     */
    public boolean borrowResources(Family needyFamily, Tribe tribe, int foodNeeded, int waterNeeded) {
        // Get all other families sorted by total resources (richest first)
        List<Family> otherFamilies = tribe.getFamilies().stream()
            .filter(f -> !f.getId().equals(needyFamily.getId()))
            .sorted((f1, f2) -> {
                int total1 = f1.getStorage().getFood() + f1.getStorage().getWater();
                int total2 = f2.getStorage().getFood() + f2.getStorage().getWater();
                return Integer.compare(total2, total1);
            })
            .toList();
        
        for (Family richFamily : otherFamilies) {
            // Check if rich family has surplus
            int surplus = richFamily.getStorage().getFood() - (richFamily.getMembers().size() * 6);
            if (surplus <= 0) continue;
            
            // Sharing success based on bond level (0-100 scale)
            int bondChance = tribe.getBondLevel();
            boolean sharingSucceeds = random.nextInt(100) < bondChance;
            
            if (sharingSucceeds) {
                // Transfer resources
                int foodToGive = Math.min(foodNeeded, richFamily.getStorage().getFood() / 2);
                int waterToGive = Math.min(waterNeeded, richFamily.getStorage().getWater() / 2);
                
                richFamily.getStorage().setFood(richFamily.getStorage().getFood() - foodToGive);
                richFamily.getStorage().setWater(richFamily.getStorage().getWater() - waterToGive);
                
                needyFamily.getStorage().setFood(needyFamily.getStorage().getFood() + foodToGive);
                needyFamily.getStorage().setWater(needyFamily.getStorage().getWater() + waterToGive);
                
                // Increase bond on successful sharing
                tribe.setBondLevel(Math.min(100, tribe.getBondLevel() + 1));
                return true;
            } else {
                // Decrease bond on failed sharing
                tribe.setBondLevel(Math.max(0, tribe.getBondLevel() - 2));
            }
        }
        
        return false;
    }

    /**
     * Access central storage if enabled and family storage is depleted.
     */
    public boolean accessCentralStorage(Family family, Tribe tribe, int foodNeeded, int waterNeeded) {
        if (!tribe.getPolicy().isEnableCentralStorage() || tribe.getCentralStorage() == null) {
            return false;
        }
        
        Resources central = tribe.getCentralStorage();
        int foodToGive = Math.min(foodNeeded, central.getFood());
        int waterToGive = Math.min(waterNeeded, central.getWater());
        
        central.setFood(central.getFood() - foodToGive);
        central.setWater(central.getWater() - waterToGive);
        
        family.getStorage().setFood(family.getStorage().getFood() + foodToGive);
        family.getStorage().setWater(family.getStorage().getWater() + waterToGive);
        
        return foodToGive > 0 || waterToGive > 0;
    }

    /**
     * Determine which family member suffers health loss based on sharing priority.
     */
    public Person selectMemberToSuffer(Family family, Policy policy) {
        List<Person> members = family.getMembers();
        if (members.isEmpty()) return null;
        
        Policy.SharingPriority priority = policy.getSharingPriority();
        
        return switch (priority) {
            case ELDER -> members.stream()
                .filter(p -> p.getRole() == Person.PersonRole.ELDER)
                .findFirst()
                .orElse(members.stream().max(Comparator.comparingInt(Person::getAge)).orElse(null));
            case CHILD -> members.stream()
                .filter(p -> p.getRole() == Person.PersonRole.CHILD)
                .findFirst()
                .orElse(members.stream().min(Comparator.comparingInt(Person::getAge)).orElse(null));
            case HUNTER -> members.stream()
                .filter(p -> p.getRole() == Person.PersonRole.HUNTER)
                .findFirst()
                .orElse(members.get(0));
            case GATHERER -> members.stream()
                .filter(p -> p.getRole() == Person.PersonRole.GATHERER)
                .findFirst()
                .orElse(members.get(0));
            case YOUNGEST -> members.stream()
                .min(Comparator.comparingInt(Person::getAge))
                .orElse(null);
            case RANDOM -> members.get(random.nextInt(members.size()));
        };
    }

    /**
     * Apply storage decay to all family storages.
     */
    public void applyStorageDecay(Tribe tribe, double decayRate) {
        for (Family family : tribe.getFamilies()) {
            Resources storage = family.getStorage();
            storage.setFood((int) (storage.getFood() * (1.0 - decayRate)));
            storage.setWater((int) (storage.getWater() * (1.0 - decayRate)));
        }
        
        // Also apply to central storage if enabled
        if (tribe.getCentralStorage() != null) {
            Resources central = tribe.getCentralStorage();
            central.setFood((int) (central.getFood() * (1.0 - decayRate)));
            central.setWater((int) (central.getWater() * (1.0 - decayRate)));
        }
    }

    public List<Family> getFamiliesByTribe(Long tribeId) {
        return familyRepository.findByTribeId(tribeId);
    }
}
