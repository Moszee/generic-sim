package com.genericsim.backend.controller;

import com.genericsim.backend.dto.PolicyUpdateDTO;
import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.dto.TribeStatisticsDTO;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.service.TribeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing hunter-gatherer tribes.
 * Provides endpoints for tribe creation, state management, statistics, and policy updates.
 */
@RestController
@RequestMapping("/api/tribes")
public class TribeController {

    private final TribeService tribeService;

    public TribeController(TribeService tribeService) {
        this.tribeService = tribeService;
    }

    /**
     * Create a new tribe with default resources, policies, and initial members.
     * 
     * @param request contains name and description for the tribe
     * @return ResponseEntity with the created tribe's state
     */
    @PostMapping
    public ResponseEntity<TribeStateDTO> createTribe(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        Tribe tribe = tribeService.createTribe(name, description);
        return ResponseEntity.ok(tribeService.getTribeState(tribe.getId()));
    }

    /**
     * Get all tribes in the system.
     * 
     * @return ResponseEntity with list of all tribe states
     */
    @GetMapping
    public ResponseEntity<List<TribeStateDTO>> getAllTribes() {
        return ResponseEntity.ok(tribeService.getAllTribes());
    }

    /**
     * Get the current state of a specific tribe.
     * Includes full details: members, resources, and policy.
     * 
     * @param id the tribe ID
     * @return ResponseEntity with the tribe's current state
     */
    @GetMapping("/{id}")
    public ResponseEntity<TribeStateDTO> getTribeState(@PathVariable Long id) {
        return ResponseEntity.ok(tribeService.getTribeState(id));
    }

    /**
     * Get aggregated statistics for a tribe.
     * Provides frontend-friendly summary including population counts,
     * role breakdown, health statistics, and resource status.
     * 
     * @param id the tribe ID
     * @return ResponseEntity with tribe statistics
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<TribeStatisticsDTO> getTribeStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(tribeService.getTribeStatistics(id));
    }

    /**
     * Update the policy settings for a tribe.
     * Allows modification of tax rates and incentives.
     * Only provided values will be updated (partial updates supported).
     * 
     * @param id the tribe ID
     * @param policyUpdate the policy parameters to update
     * @return ResponseEntity with updated tribe state
     */
    @PutMapping("/{id}/policy")
    public ResponseEntity<TribeStateDTO> updateTribePolicy(
            @PathVariable Long id,
            @RequestBody PolicyUpdateDTO policyUpdate) {
        return ResponseEntity.ok(tribeService.updateTribePolicy(id, policyUpdate));
    }

    /**
     * Advance the simulation by one day (tick) for a tribe.
     * Processes resource gathering, consumption, health updates, and aging.
     * 
     * @param id the tribe ID
     * @return ResponseEntity with updated tribe state after tick processing
     */
    @PostMapping("/{id}/tick")
    public ResponseEntity<TribeStateDTO> processTick(@PathVariable Long id) {
        return ResponseEntity.ok(tribeService.processTick(id));
    }
}
