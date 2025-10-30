package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines characteristics and requirements for a lifestyle.
 * Lifestyles determine how communities interact with resources and technologies.
 */
@Entity
@Table(name = "lifestyle_definitions")
@Getter
@Setter
@NoArgsConstructor
public class LifestyleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private LifestyleType lifestyleType;

    @Column(nullable = false)
    private double mobilityFactor = 1.0; // How mobile the community is (0-1)

    @Column(nullable = false)
    private double cohesionFactor = 1.0; // Base community cohesion multiplier

    @Column(nullable = false)
    private double maintenanceCost = 0.0; // Base maintenance cost per tick

    @ElementCollection
    @CollectionTable(name = "lifestyle_resource_modifiers",
                     joinColumns = @JoinColumn(name = "lifestyle_definition_id"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "modifier")
    private Map<ResourceType, Double> resourceGatheringModifiers = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "lifestyle_required_technologies",
                     joinColumns = @JoinColumn(name = "lifestyle_definition_id"))
    @Column(name = "technology_type")
    @Enumerated(EnumType.STRING)
    private java.util.Set<TechnologyType> requiredTechnologies = new java.util.HashSet<>();

    public LifestyleDefinition(LifestyleType lifestyleType) {
        this.lifestyleType = lifestyleType;
    }

    public LifestyleDefinition(LifestyleType lifestyleType, double mobilityFactor, double cohesionFactor) {
        this.lifestyleType = lifestyleType;
        this.mobilityFactor = mobilityFactor;
        this.cohesionFactor = cohesionFactor;
    }
}
