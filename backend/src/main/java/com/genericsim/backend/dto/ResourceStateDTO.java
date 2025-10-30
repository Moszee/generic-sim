package com.genericsim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Data Transfer Object for exporting/importing resource and coefficient state.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceStateDTO {
    
    /**
     * Map of resource ID to current value
     */
    private Map<String, Double> resources;
    
    /**
     * Map of coefficient ID to current value
     */
    private Map<String, Double> coefficients;
}
