package com.genericsim.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic storage for multiple resource types.
 * Uses a Map to allow flexible resource storage without code changes.
 */
@Entity
@Table(name = "resource_storage")
@Getter
@Setter
@NoArgsConstructor
public class ResourceStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "resource_storage_amounts",
                     joinColumns = @JoinColumn(name = "storage_id"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    private Map<ResourceType, Integer> resources = new HashMap<>();

    @OneToOne(mappedBy = "genericStorage")
    private Tribe tribe;

    @OneToOne(mappedBy = "genericCentralStorage")
    private Tribe centralTribe;

    /**
     * Get amount of a specific resource type
     */
    public int getAmount(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    /**
     * Set amount of a specific resource type
     */
    public void setAmount(ResourceType type, int amount) {
        resources.put(type, Math.max(0, amount));
    }

    /**
     * Add amount to a specific resource type
     */
    public void addAmount(ResourceType type, int amount) {
        int current = getAmount(type);
        setAmount(type, current + amount);
    }

    /**
     * Remove amount from a specific resource type
     * Returns true if successful, false if insufficient resources
     */
    public boolean removeAmount(ResourceType type, int amount) {
        int current = getAmount(type);
        if (current >= amount) {
            setAmount(type, current - amount);
            return true;
        }
        return false;
    }

    /**
     * Check if storage has at least the specified amount
     */
    public boolean hasAmount(ResourceType type, int amount) {
        return getAmount(type) >= amount;
    }

    /**
     * Initialize with default amounts for all resource types
     */
    public void initializeDefaults() {
        for (ResourceType type : ResourceType.values()) {
            resources.putIfAbsent(type, 0);
        }
    }
}
