package com.genericsim.backend.dto;

import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Tribe;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for tribe statistics.
 * Provides a frontend-friendly summary of tribe metrics including population,
 * resources, and role distribution.
 */
public class TribeStatisticsDTO {
    private Long tribeId;
    private String tribeName;
    private long currentTick;
    
    // Population statistics
    private int totalPopulation;
    private RoleBreakdown roleBreakdown;
    private HealthStats healthStats;
    
    // Resource statistics
    private ResourceStats resourceStats;
    
    // Policy information
    private PolicySummary policySummary;
    
    /**
     * Role breakdown showing count of members per role
     */
    public static class RoleBreakdown {
        private int hunters;
        private int gatherers;
        private int children;
        private int elders;
        
        public RoleBreakdown(List<Person> members) {
            Map<Person.PersonRole, Long> roleCounts = members.stream()
                .collect(Collectors.groupingBy(Person::getRole, Collectors.counting()));
            
            this.hunters = roleCounts.getOrDefault(Person.PersonRole.HUNTER, 0L).intValue();
            this.gatherers = roleCounts.getOrDefault(Person.PersonRole.GATHERER, 0L).intValue();
            this.children = roleCounts.getOrDefault(Person.PersonRole.CHILD, 0L).intValue();
            this.elders = roleCounts.getOrDefault(Person.PersonRole.ELDER, 0L).intValue();
        }
        
        public int getHunters() { return hunters; }
        public void setHunters(int hunters) { this.hunters = hunters; }
        public int getGatherers() { return gatherers; }
        public void setGatherers(int gatherers) { this.gatherers = gatherers; }
        public int getChildren() { return children; }
        public void setChildren(int children) { this.children = children; }
        public int getElders() { return elders; }
        public void setElders(int elders) { this.elders = elders; }
    }
    
    /**
     * Health statistics for the tribe
     */
    public static class HealthStats {
        private int averageHealth;
        private int minHealth;
        private int maxHealth;
        private int healthyMembers;
        
        public HealthStats(List<Person> members) {
            if (members.isEmpty()) {
                this.averageHealth = 0;
                this.minHealth = 0;
                this.maxHealth = 0;
                this.healthyMembers = 0;
            } else {
                this.averageHealth = (int) members.stream()
                    .mapToInt(Person::getHealth)
                    .average()
                    .orElse(0);
                this.minHealth = members.stream()
                    .mapToInt(Person::getHealth)
                    .min()
                    .orElse(0);
                this.maxHealth = members.stream()
                    .mapToInt(Person::getHealth)
                    .max()
                    .orElse(0);
                this.healthyMembers = (int) members.stream()
                    .filter(p -> p.getHealth() >= 70)
                    .count();
            }
        }
        
        public int getAverageHealth() { return averageHealth; }
        public void setAverageHealth(int averageHealth) { this.averageHealth = averageHealth; }
        public int getMinHealth() { return minHealth; }
        public void setMinHealth(int minHealth) { this.minHealth = minHealth; }
        public int getMaxHealth() { return maxHealth; }
        public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
        public int getHealthyMembers() { return healthyMembers; }
        public void setHealthyMembers(int healthyMembers) { this.healthyMembers = healthyMembers; }
    }
    
    /**
     * Resource statistics
     */
    public static class ResourceStats {
        private int food;
        private int water;
        private String resourceStatus;
        
        public ResourceStats(int food, int water, int population) {
            this.food = food;
            this.water = water;
            
            // Calculate status based on population needs
            int foodPerPerson = population > 0 ? food / population : 0;
            int waterPerPerson = population > 0 ? water / population : 0;
            
            if (foodPerPerson >= 10 && waterPerPerson >= 10) {
                this.resourceStatus = "ABUNDANT";
            } else if (foodPerPerson >= 5 && waterPerPerson >= 5) {
                this.resourceStatus = "ADEQUATE";
            } else if (foodPerPerson >= 3 && waterPerPerson >= 3) {
                this.resourceStatus = "LOW";
            } else {
                this.resourceStatus = "CRITICAL";
            }
        }
        
        public int getFood() { return food; }
        public void setFood(int food) { this.food = food; }
        public int getWater() { return water; }
        public void setWater(int water) { this.water = water; }
        public String getResourceStatus() { return resourceStatus; }
        public void setResourceStatus(String resourceStatus) { this.resourceStatus = resourceStatus; }
    }
    
    /**
     * Policy summary
     */
    public static class PolicySummary {
        private int foodTaxRate;
        private int waterTaxRate;
        private int huntingIncentive;
        private int gatheringIncentive;
        
        public PolicySummary(int foodTaxRate, int waterTaxRate, int huntingIncentive, int gatheringIncentive) {
            this.foodTaxRate = foodTaxRate;
            this.waterTaxRate = waterTaxRate;
            this.huntingIncentive = huntingIncentive;
            this.gatheringIncentive = gatheringIncentive;
        }
        
        public int getFoodTaxRate() { return foodTaxRate; }
        public void setFoodTaxRate(int foodTaxRate) { this.foodTaxRate = foodTaxRate; }
        public int getWaterTaxRate() { return waterTaxRate; }
        public void setWaterTaxRate(int waterTaxRate) { this.waterTaxRate = waterTaxRate; }
        public int getHuntingIncentive() { return huntingIncentive; }
        public void setHuntingIncentive(int huntingIncentive) { this.huntingIncentive = huntingIncentive; }
        public int getGatheringIncentive() { return gatheringIncentive; }
        public void setGatheringIncentive(int gatheringIncentive) { this.gatheringIncentive = gatheringIncentive; }
    }
    
    /**
     * Constructs statistics DTO from a Tribe entity
     */
    public TribeStatisticsDTO(Tribe tribe) {
        this.tribeId = tribe.getId();
        this.tribeName = tribe.getName();
        this.currentTick = tribe.getCurrentTick();
        this.totalPopulation = tribe.getMembers().size();
        this.roleBreakdown = new RoleBreakdown(tribe.getMembers());
        this.healthStats = new HealthStats(tribe.getMembers());
        this.resourceStats = new ResourceStats(
            tribe.getResources() != null ? tribe.getResources().getFood() : 0,
            tribe.getResources() != null ? tribe.getResources().getWater() : 0,
            this.totalPopulation
        );
        this.policySummary = tribe.getPolicy() != null ? new PolicySummary(
            tribe.getPolicy().getFoodTaxRate(),
            tribe.getPolicy().getWaterTaxRate(),
            tribe.getPolicy().getHuntingIncentive(),
            tribe.getPolicy().getGatheringIncentive()
        ) : null;
    }
    
    public TribeStatisticsDTO() {
    }
    
    public Long getTribeId() { return tribeId; }
    public void setTribeId(Long tribeId) { this.tribeId = tribeId; }
    public String getTribeName() { return tribeName; }
    public void setTribeName(String tribeName) { this.tribeName = tribeName; }
    public long getCurrentTick() { return currentTick; }
    public void setCurrentTick(long currentTick) { this.currentTick = currentTick; }
    public int getTotalPopulation() { return totalPopulation; }
    public void setTotalPopulation(int totalPopulation) { this.totalPopulation = totalPopulation; }
    public RoleBreakdown getRoleBreakdown() { return roleBreakdown; }
    public void setRoleBreakdown(RoleBreakdown roleBreakdown) { this.roleBreakdown = roleBreakdown; }
    public HealthStats getHealthStats() { return healthStats; }
    public void setHealthStats(HealthStats healthStats) { this.healthStats = healthStats; }
    public ResourceStats getResourceStats() { return resourceStats; }
    public void setResourceStats(ResourceStats resourceStats) { this.resourceStats = resourceStats; }
    public PolicySummary getPolicySummary() { return policySummary; }
    public void setPolicySummary(PolicySummary policySummary) { this.policySummary = policySummary; }
}
