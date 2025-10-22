package com.genericsim.backend.dto;

import com.genericsim.backend.model.Person;
import com.genericsim.backend.model.Tribe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for tribe statistics.
 * Provides a frontend-friendly summary of tribe metrics including population,
 * resources, and role distribution.
 */
@Getter
@Setter
@NoArgsConstructor
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
    @Getter
    @Setter
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
    }
    
    /**
     * Health statistics for the tribe
     */
    @Getter
    @Setter
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
    }
    
    /**
     * Resource statistics
     */
    @Getter
    @Setter
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
    }
    
    /**
     * Policy summary
     */
    @Getter
    @Setter
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
}
