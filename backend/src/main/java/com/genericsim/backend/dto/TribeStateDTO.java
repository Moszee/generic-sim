package com.genericsim.backend.dto;

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
    private ResourcesDTO resources;
    private PolicyDTO policy;
    private List<PersonDTO> members;

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

        public PolicyDTO(Policy policy) {
            if (policy != null) {
                this.name = policy.getName();
                this.description = policy.getDescription();
                this.foodTaxRate = policy.getFoodTaxRate();
                this.waterTaxRate = policy.getWaterTaxRate();
                this.huntingIncentive = policy.getHuntingIncentive();
                this.gatheringIncentive = policy.getGatheringIncentive();
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
    }

    public static class PersonDTO {
        private Long id;
        private String name;
        private String role;
        private int age;
        private int health;

        public PersonDTO(Person person) {
            this.id = person.getId();
            this.name = person.getName();
            this.role = person.getRole().toString();
            this.age = person.getAge();
            this.health = person.getHealth();
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
}
