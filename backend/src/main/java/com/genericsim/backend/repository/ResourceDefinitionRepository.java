package com.genericsim.backend.repository;

import com.genericsim.backend.model.ResourceDefinition;
import com.genericsim.backend.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceDefinitionRepository extends JpaRepository<ResourceDefinition, Long> {
    Optional<ResourceDefinition> findByResourceType(ResourceType resourceType);
}
