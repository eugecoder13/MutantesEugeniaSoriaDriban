package com.magneto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magneto.dto.DnaRequest;
import com.magneto.dto.StatsResponse;
import com.magneto.service.MutantService;
import com.magneto.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MutantController.
 */
@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Test
    @DisplayName("POST /mutant should return 200 OK for mutant DNA")
    void testDetectMutantReturns200() throws Exception {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.analyzeDna(any())).thenReturn(true);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isMutant").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /mutant should return 403 FORBIDDEN for human DNA")
    void testDetectHumanReturns403() throws Exception {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        DnaRequest request = new DnaRequest(dna);

        when(mutantService.analyzeDna(any())).thenReturn(false);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.isMutant").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /mutant should return 400 for invalid DNA - non NxN matrix")
    void testDetectMutantWithInvalidMatrix() throws Exception {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATG"  // Invalid: not same length
        };
        DnaRequest request = new DnaRequest(dna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant should return 400 for invalid DNA - invalid characters")
    void testDetectMutantWithInvalidCharacters() throws Exception {
        String[] dna = {
            "ATGCGA",
            "CAXTGC",  // Invalid: contains 'X'
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        DnaRequest request = new DnaRequest(dna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant should return 400 for null DNA")
    void testDetectMutantWithNullDna() throws Exception {
        DnaRequest request = new DnaRequest(null);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant should return 400 for empty DNA array")
    void testDetectMutantWithEmptyDna() throws Exception {
        String[] dna = {};
        DnaRequest request = new DnaRequest(dna);

        mockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats should return 200 with statistics")
    void testGetStatsReturns200() throws Exception {
        StatsResponse stats = new StatsResponse(40L, 100L, 0.4);

        when(statsService.getVerificationStats()).thenReturn(stats);

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    @Test
    @DisplayName("GET /health should return 200")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mutant Detection API is running"));
    }
}
