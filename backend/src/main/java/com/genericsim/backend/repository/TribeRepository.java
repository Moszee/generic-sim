package com.genericsim.backend.repository;

import com.genericsim.backend.model.Tribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TribeRepository extends JpaRepository<Tribe, Long> {
}
