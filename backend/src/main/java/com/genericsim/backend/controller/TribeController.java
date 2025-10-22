package com.genericsim.backend.controller;

import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.service.TribeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tribes")
public class TribeController {

    private final TribeService tribeService;

    public TribeController(TribeService tribeService) {
        this.tribeService = tribeService;
    }

    @PostMapping
    public ResponseEntity<TribeStateDTO> createTribe(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        Tribe tribe = tribeService.createTribe(name, description);
        return ResponseEntity.ok(tribeService.getTribeState(tribe.getId()));
    }

    @GetMapping
    public ResponseEntity<List<TribeStateDTO>> getAllTribes() {
        return ResponseEntity.ok(tribeService.getAllTribes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TribeStateDTO> getTribeState(@PathVariable Long id) {
        return ResponseEntity.ok(tribeService.getTribeState(id));
    }

    @PostMapping("/{id}/tick")
    public ResponseEntity<TribeStateDTO> processTick(@PathVariable Long id) {
        return ResponseEntity.ok(tribeService.processTick(id));
    }
}
