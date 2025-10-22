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

    private Long extractTribeId(String json) {
        // Simple extraction of tribeId from JSON response
        int start = json.indexOf("\"tribeId\":") + 10;
        int end = json.indexOf(",", start);
        return Long.parseLong(json.substring(start, end));
    }
}
