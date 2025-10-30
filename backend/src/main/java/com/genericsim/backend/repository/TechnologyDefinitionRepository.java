package com.genericsim.backend.repository;

import com.genericsim.backend.model.TechnologyDefinition;
import com.genericsim.backend.model.TechnologyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnologyDefinitionRepository extends JpaRepository<TechnologyDefinition, Long> {
    Optional<TechnologyDefinition> findByTechnologyType(TechnologyType technologyType);
}
