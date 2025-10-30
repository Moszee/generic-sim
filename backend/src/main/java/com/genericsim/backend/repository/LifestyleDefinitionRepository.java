package com.genericsim.backend.repository;

import com.genericsim.backend.model.LifestyleDefinition;
import com.genericsim.backend.model.LifestyleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LifestyleDefinitionRepository extends JpaRepository<LifestyleDefinition, Long> {
    Optional<LifestyleDefinition> findByLifestyleType(LifestyleType lifestyleType);
}
