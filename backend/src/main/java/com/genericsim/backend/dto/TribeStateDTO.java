package com.genericsim.backend.dto;

import com.genericsim.backend.model.Family;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Policy;
import com.genericsim.backend.model.Resources;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TribeStateDTO {
    private Long tribeId;
    private String tribeName;
    private String description;
    private long currentTick;
    private int bondLevel;
    private ResourcesDTO resources;
    private ResourcesDTO centralStorage;
    private PolicyDTO policy;
    private List<PersonDTO> members;
    private List<FamilyDTO> families;

    @Getter
    @Setter
    public static class ResourcesDTO {
        private int food;
        private int water;

        public ResourcesDTO(Resources resources) {
            if (resources != null) {
                this.food = resources.getFood();
                this.water = resources.getWater();
            }
        }
    }

    @Getter
    @Setter
    public static class PolicyDTO {
        private String name;
        private String description;
        private int foodTaxRate;
        private int waterTaxRate;
        private int huntingIncentive;
        private int gatheringIncentive;
        private String sharingPriority;
        private boolean enableCentralStorage;
        private int centralStorageTaxRate;
        private double storageDecayRate;
        private int storageDecayInterval;

        public PolicyDTO(Policy policy) {
            if (policy != null) {
                this.name = policy.getName();
                this.description = policy.getDescription();
                this.foodTaxRate = policy.getFoodTaxRate();
                this.waterTaxRate = policy.getWaterTaxRate();
                this.huntingIncentive = policy.getHuntingIncentive();
                this.gatheringIncentive = policy.getGatheringIncentive();
                this.sharingPriority = policy.getSharingPriority().name();
                this.enableCentralStorage = policy.isEnableCentralStorage();
                this.centralStorageTaxRate = policy.getCentralStorageTaxRate();
                this.storageDecayRate = policy.getStorageDecayRate();
                this.storageDecayInterval = policy.getStorageDecayInterval();
            }
        }
    }

    @Getter
    @Setter
    public static class PersonDTO {
        private Long id;
        private String name;
        private String role;
        private int age;
        private int health;
        private double huntingSkill;
        private double gatheringSkill;
        private Long familyId;

        public PersonDTO(Person person) {
            this.id = person.getId();
            this.name = person.getName();
            this.role = person.getRole().toString();
            this.age = person.getAge();
            this.health = person.getHealth();
            this.huntingSkill = person.getHuntingSkill();
            this.gatheringSkill = person.getGatheringSkill();
            this.familyId = person.getFamily() != null ? person.getFamily().getId() : null;
        }
    }

    @Getter
    @Setter
    public static class FamilyDTO {
        private Long id;
        private String name;
        private ResourcesDTO storage;
        private int memberCount;

        public FamilyDTO(Family family) {
            this.id = family.getId();
            this.name = family.getName();
            this.storage = new ResourcesDTO(family.getStorage());
            this.memberCount = family.getMembers().size();
        }
    }
}
