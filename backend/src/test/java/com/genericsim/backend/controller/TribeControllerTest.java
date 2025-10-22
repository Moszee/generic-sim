package com.genericsim.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TribeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateTribe() throws Exception {
        String requestBody = "{\"name\":\"Test Tribe\",\"description\":\"A test tribe\"}";
        
        mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tribeName").value("Test Tribe"))
                .andExpect(jsonPath("$.description").value("A test tribe"))
                .andExpect(jsonPath("$.currentTick").value(0))
                .andExpect(jsonPath("$.resources").exists())
                .andExpect(jsonPath("$.policy").exists())
                .andExpect(jsonPath("$.members").isArray());
    }

    @Test
    public void testGetAllTribes() throws Exception {
        mockMvc.perform(get("/api/tribes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testProcessTick() throws Exception {
        // First create a tribe
        String requestBody = "{\"name\":\"Test Tribe\",\"description\":\"A test tribe\"}";
        MvcResult createResult = mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long tribeId = extractTribeId(response);

        // Process a tick
        mockMvc.perform(post("/api/tribes/" + tribeId + "/tick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTick").value(1))
                .andExpect(jsonPath("$.resources").exists())
                .andExpect(jsonPath("$.members").isArray());
    }

    @Test
    public void testGetTribeState() throws Exception {
        // First create a tribe
        String requestBody = "{\"name\":\"Test Tribe\",\"description\":\"A test tribe\"}";
        MvcResult createResult = mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long tribeId = extractTribeId(response);

        // Get tribe state
        mockMvc.perform(get("/api/tribes/" + tribeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tribeName").value("Test Tribe"))
                .andExpect(jsonPath("$.resources").exists())
                .andExpect(jsonPath("$.members").isArray());
    }

    @Test
    public void testGetTribeStatistics() throws Exception {
        // First create a tribe
        String requestBody = "{\"name\":\"Stats Test Tribe\",\"description\":\"A tribe for stats testing\"}";
        MvcResult createResult = mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long tribeId = extractTribeId(response);

        // Get tribe statistics
        mockMvc.perform(get("/api/tribes/" + tribeId + "/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tribeId").value(tribeId))
                .andExpect(jsonPath("$.tribeName").value("Stats Test Tribe"))
                .andExpect(jsonPath("$.totalPopulation").value(6))
                .andExpect(jsonPath("$.roleBreakdown").exists())
                .andExpect(jsonPath("$.roleBreakdown.hunters").value(2))
                .andExpect(jsonPath("$.roleBreakdown.gatherers").value(2))
                .andExpect(jsonPath("$.roleBreakdown.children").value(1))
                .andExpect(jsonPath("$.roleBreakdown.elders").value(1))
                .andExpect(jsonPath("$.healthStats").exists())
                .andExpect(jsonPath("$.healthStats.averageHealth").exists())
                .andExpect(jsonPath("$.resourceStats").exists())
                .andExpect(jsonPath("$.resourceStats.food").value(100))
                .andExpect(jsonPath("$.resourceStats.water").value(100))
                .andExpect(jsonPath("$.resourceStats.resourceStatus").exists())
                .andExpect(jsonPath("$.policySummary").exists())
                .andExpect(jsonPath("$.policySummary.foodTaxRate").value(10))
                .andExpect(jsonPath("$.policySummary.waterTaxRate").value(10));
    }

    @Test
    public void testUpdateTribePolicy() throws Exception {
        // First create a tribe
        String requestBody = "{\"name\":\"Policy Test Tribe\",\"description\":\"A tribe for policy testing\"}";
        MvcResult createResult = mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long tribeId = extractTribeId(response);

        // Update policy
        String policyUpdate = "{\"foodTaxRate\":15,\"waterTaxRate\":20,\"huntingIncentive\":8,\"gatheringIncentive\":10}";
        mockMvc.perform(put("/api/tribes/" + tribeId + "/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(policyUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policy.foodTaxRate").value(15))
                .andExpect(jsonPath("$.policy.waterTaxRate").value(20))
                .andExpect(jsonPath("$.policy.huntingIncentive").value(8))
                .andExpect(jsonPath("$.policy.gatheringIncentive").value(10));
    }

    @Test
    public void testUpdateTribePolicyPartial() throws Exception {
        // First create a tribe
        String requestBody = "{\"name\":\"Partial Policy Test Tribe\",\"description\":\"A tribe for partial policy testing\"}";
        MvcResult createResult = mockMvc.perform(post("/api/tribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long tribeId = extractTribeId(response);

        // Update only some policy fields
        String policyUpdate = "{\"foodTaxRate\":25,\"huntingIncentive\":12}";
        mockMvc.perform(put("/api/tribes/" + tribeId + "/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(policyUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policy.foodTaxRate").value(25))
                .andExpect(jsonPath("$.policy.waterTaxRate").value(10)) // Should remain unchanged
                .andExpect(jsonPath("$.policy.huntingIncentive").value(12))
                .andExpect(jsonPath("$.policy.gatheringIncentive").value(5)); // Should remain unchanged
    }

    private Long extractTribeId(String json) {
        // Simple extraction of tribeId from JSON response
        int start = json.indexOf("\"tribeId\":") + 10;
        int end = json.indexOf(",", start);
        return Long.parseLong(json.substring(start, end));
    }
}
