package com.magneto.controller;

import com.magneto.dto.DnaRequest;
import com.magneto.dto.DnaResponse;
import com.magneto.dto.StatsResponse;
import com.magneto.service.MutantService;
import com.magneto.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for mutant detection and statistics endpoints.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mutant Detection API", description = "Endpoints for DNA analysis and statistics")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @PostMapping("/mutant")
    @Operation(
        summary = "Detect if DNA belongs to a mutant",
        description = "Analyzes a DNA sequence to determine if it belongs to a mutant. " +
                      "Returns 200 OK for mutants, 403 Forbidden for humans."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "DNA belongs to a mutant",
            content = @Content(schema = @Schema(implementation = DnaResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "DNA belongs to a human",
            content = @Content(schema = @Schema(implementation = DnaResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid DNA sequence format"
        )
    })
    public ResponseEntity<DnaResponse> detectMutant(@Valid @RequestBody DnaRequest dnaRequest) {
        log.info("Received mutant detection request");

        boolean isMutant = mutantService.analyzeDna(dnaRequest.getDna());

        if (isMutant) {
            DnaResponse response = new DnaResponse(true, "Mutant DNA detected successfully");
            return ResponseEntity.ok(response);
        } else {
            DnaResponse response = new DnaResponse(false, "Human DNA detected");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Get DNA verification statistics",
        description = "Returns statistics about mutant and human DNA verifications, including counts and ratio"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = StatsResponse.class))
        )
    })
    public ResponseEntity<StatsResponse> getStatistics() {
        log.info("Received statistics request");

        StatsResponse stats = statsService.getVerificationStats();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Checks if the service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Mutant Detection API is running");
    }
}
