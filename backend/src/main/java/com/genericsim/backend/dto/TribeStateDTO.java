package com.genericsim.backend.dto;

import com.genericsim.backend.model.Family;
import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Policy;
import com.genericsim.backend.model.Resources;

import java.util.List;
import java.util.stream.Collectors;

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

    public static class ResourcesDTO {
        private int food;
        private int water;

        public ResourcesDTO(Resources resources) {
            if (resources != null) {
                this.food = resources.getFood();
                this.water = resources.getWater();
            }
        }

        public int getFood() { return food; }
        public void setFood(int food) { this.food = food; }
        public int getWater() { return water; }
        public void setWater(int water) { this.water = water; }
    }

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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getFoodTaxRate() { return foodTaxRate; }
        public void setFoodTaxRate(int foodTaxRate) { this.foodTaxRate = foodTaxRate; }
        public int getWaterTaxRate() { return waterTaxRate; }
        public void setWaterTaxRate(int waterTaxRate) { this.waterTaxRate = waterTaxRate; }
        public int getHuntingIncentive() { return huntingIncentive; }
        public void setHuntingIncentive(int huntingIncentive) { this.huntingIncentive = huntingIncentive; }
        public int getGatheringIncentive() { return gatheringIncentive; }
        public void setGatheringIncentive(int gatheringIncentive) { this.gatheringIncentive = gatheringIncentive; }
        public String getSharingPriority() { return sharingPriority; }
        public void setSharingPriority(String sharingPriority) { this.sharingPriority = sharingPriority; }
        public boolean isEnableCentralStorage() { return enableCentralStorage; }
        public void setEnableCentralStorage(boolean enableCentralStorage) { this.enableCentralStorage = enableCentralStorage; }
        public int getCentralStorageTaxRate() { return centralStorageTaxRate; }
        public void setCentralStorageTaxRate(int centralStorageTaxRate) { this.centralStorageTaxRate = centralStorageTaxRate; }
        public double getStorageDecayRate() { return storageDecayRate; }
        public void setStorageDecayRate(double storageDecayRate) { this.storageDecayRate = storageDecayRate; }
        public int getStorageDecayInterval() { return storageDecayInterval; }
        public void setStorageDecayInterval(int storageDecayInterval) { this.storageDecayInterval = storageDecayInterval; }
    }

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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public int getHealth() { return health; }
        public void setHealth(int health) { this.health = health; }
        public double getHuntingSkill() { return huntingSkill; }
        public void setHuntingSkill(double huntingSkill) { this.huntingSkill = huntingSkill; }
        public double getGatheringSkill() { return gatheringSkill; }
        public void setGatheringSkill(double gatheringSkill) { this.gatheringSkill = gatheringSkill; }
        public Long getFamilyId() { return familyId; }
        public void setFamilyId(Long familyId) { this.familyId = familyId; }
    }

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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public ResourcesDTO getStorage() { return storage; }
        public void setStorage(ResourcesDTO storage) { this.storage = storage; }
        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    }

    public TribeStateDTO() {
    }

    public Long getTribeId() { return tribeId; }
    public void setTribeId(Long tribeId) { this.tribeId = tribeId; }
    public String getTribeName() { return tribeName; }
    public void setTribeName(String tribeName) { this.tribeName = tribeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getCurrentTick() { return currentTick; }
    public void setCurrentTick(long currentTick) { this.currentTick = currentTick; }
    public ResourcesDTO getResources() { return resources; }
    public void setResources(ResourcesDTO resources) { this.resources = resources; }
    public PolicyDTO getPolicy() { return policy; }
    public void setPolicy(PolicyDTO policy) { this.policy = policy; }
    public List<PersonDTO> getMembers() { return members; }
    public void setMembers(List<PersonDTO> members) { this.members = members; }
    public int getBondLevel() { return bondLevel; }
    public void setBondLevel(int bondLevel) { this.bondLevel = bondLevel; }
    public ResourcesDTO getCentralStorage() { return centralStorage; }
    public void setCentralStorage(ResourcesDTO centralStorage) { this.centralStorage = centralStorage; }
    public List<FamilyDTO> getFamilies() { return families; }
    public void setFamilies(List<FamilyDTO> families) { this.families = families; }
}
