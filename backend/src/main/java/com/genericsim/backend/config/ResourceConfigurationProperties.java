package com.genericsim.backend.config;

import com.genericsim.backend.model.ResourceOrCoefficientConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for loading resources and coefficients from YAML/JSON.
 * Binds to application.yml under the 'simulation' prefix.
 */
@Configuration
@ConfigurationProperties(prefix = "simulation")
@Getter
@Setter
public class ResourceConfigurationProperties {
    
    /**
     * List of resource configurations
     */
    private List<ResourceOrCoefficientConfig> resources = new ArrayList<>();
    
    /**
     * List of coefficient configurations
     */
    private List<ResourceOrCoefficientConfig> coefficients = new ArrayList<>();
}
