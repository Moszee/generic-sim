package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Defines characteristics and coefficients for a resource type.
 * Allows for flexible configuration of resource behavior without code changes.
 */
@Entity
@Table(name = "resource_definitions")
@Getter
@Setter
@NoArgsConstructor
public class ResourceDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(nullable = false)
    private double gatheringRate = 1.0; // Base gathering rate multiplier

    @Column(nullable = false)
    private double consumptionRate = 0.0; // Per-person consumption per tick

    @Column(nullable = false)
    private double decayRate = 0.1; // Storage decay rate per interval

    @Column(nullable = false)
    private int storageCapacity = 1000; // Maximum storage capacity

    @Column(nullable = false)
    private double weight = 1.0; // Weight for carrying/transport

    @Column(nullable = false)
    private boolean renewable = true; // Can be gathered/regenerated

    public ResourceDefinition(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceDefinition(ResourceType resourceType, double gatheringRate, double consumptionRate) {
        this.resourceType = resourceType;
        this.gatheringRate = gatheringRate;
        this.consumptionRate = consumptionRate;
    }
}
