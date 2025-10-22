package com.genericsim.backend.service;

import com.genericsim.backend.dto.TribeStateDTO;
import com.genericsim.backend.model.Tribe;
import com.genericsim.backend.repository.TribeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TribeServiceTest {

    @Autowired
    private TribeService tribeService;

    @Autowired
    private TribeRepository tribeRepository;

    @Test
    public void testCreateTribe() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        assertNotNull(tribe);
        assertNotNull(tribe.getId());
        assertEquals("Test Tribe", tribe.getName());
        assertEquals("A test tribe", tribe.getDescription());
        assertEquals(0, tribe.getCurrentTick());
        assertNotNull(tribe.getResources());
        assertNotNull(tribe.getPolicy());
        assertEquals(6, tribe.getMembers().size());
    }

    @Test
    public void testProcessTick() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStateDTO initialState = tribeService.getTribeState(tribeId);
        assertEquals(0, initialState.getCurrentTick());
        
        TribeStateDTO newState = tribeService.processTick(tribeId);
        
        assertEquals(1, newState.getCurrentTick());
        assertNotNull(newState.getResources());
        assertNotNull(newState.getMembers());
    }

    @Test
    public void testGetTribeState() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        
        TribeStateDTO state = tribeService.getTribeState(tribe.getId());
        
        assertNotNull(state);
        assertEquals("Test Tribe", state.getTribeName());
        assertEquals("A test tribe", state.getDescription());
        assertNotNull(state.getResources());
        assertNotNull(state.getPolicy());
        assertNotNull(state.getMembers());
    }

    @Test
    public void testResourcesChangeAfterTick() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        TribeStateDTO initialState = tribeService.getTribeState(tribeId);
        int initialFood = initialState.getResources().getFood();
        
        tribeService.processTick(tribeId);
        
        TribeStateDTO newState = tribeService.getTribeState(tribeId);
        int newFood = newState.getResources().getFood();
        
        // Resources should change (increase from gathering, decrease from consumption)
        assertNotEquals(initialFood, newFood);
    }

    @Test
    public void testMultipleTicks() {
        Tribe tribe = tribeService.createTribe("Test Tribe", "A test tribe");
        Long tribeId = tribe.getId();
        
        for (int i = 0; i < 5; i++) {
            tribeService.processTick(tribeId);
        }
        
        TribeStateDTO state = tribeService.getTribeState(tribeId);
        assertEquals(5, state.getCurrentTick());
    }
}
