package com.genericsim.backend.service;

import com.genericsim.backend.model.Simulation;
import com.genericsim.backend.repository.SimulationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService {

    private final SimulationRepository simulationRepository;

    public SimulationService(SimulationRepository simulationRepository) {
        this.simulationRepository = simulationRepository;
    }

    public List<Simulation> getAllSimulations() {
        return simulationRepository.findAll();
    }

    public Simulation createSimulation(Simulation simulation) {
        return simulationRepository.save(simulation);
    }

}
