package com.genericsim.backend.repository;

import com.genericsim.backend.model.GenericResourceStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericResourceStorageRepository extends JpaRepository<GenericResourceStorage, Long> {
}
