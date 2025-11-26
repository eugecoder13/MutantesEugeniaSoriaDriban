package com.magneto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for DNA verification statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Statistics of DNA verifications")
public class StatsResponse {

    @JsonProperty("count_mutant_dna")
    @Schema(description = "Total number of mutant DNA sequences verified", example = "40")
    private long countMutantDna;

    @JsonProperty("count_human_dna")
    @Schema(description = "Total number of human DNA sequences verified", example = "100")
    private long countHumanDna;

    @JsonProperty("ratio")
    @Schema(description = "Ratio of mutant DNA to human DNA", example = "0.4")
    private double ratio;
}
