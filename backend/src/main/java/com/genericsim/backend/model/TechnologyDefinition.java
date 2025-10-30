package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines characteristics and requirements for a technology.
 * Technologies can have costs, prerequisites, and effects on resource gathering.
 */
@Entity
@Table(name = "technology_definitions")
@Getter
@Setter
@NoArgsConstructor
public class TechnologyDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private TechnologyType technologyType;

    @Column(nullable = false)
    private int researchCost = 100; // Progress points needed to unlock

    @Column(nullable = false)
    private double stabilityBonus = 0.0; // Bonus to tribe stability

    @Column(nullable = false)
    private double efficiencyBonus = 0.0; // Bonus to resource gathering efficiency

    @ElementCollection
    @CollectionTable(name = "technology_resource_costs", 
                     joinColumns = @JoinColumn(name = "technology_definition_id"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "cost")
    private Map<ResourceType, Integer> resourceCosts = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "technology_prerequisites",
                     joinColumns = @JoinColumn(name = "technology_definition_id"))
    @Column(name = "prerequisite_type")
    @Enumerated(EnumType.STRING)
    private java.util.Set<TechnologyType> prerequisites = new java.util.HashSet<>();

    public TechnologyDefinition(TechnologyType technologyType) {
        this.technologyType = technologyType;
    }

    public TechnologyDefinition(TechnologyType technologyType, int researchCost) {
        this.technologyType = technologyType;
        this.researchCost = researchCost;
    }
}
