package com.magneto.dto;

import com.magneto.validation.ValidDnaSequence;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for DNA sequence verification request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DNA sequence request for mutant verification")
public class DnaRequest {

    @NotNull(message = "DNA sequence cannot be null")
    @NotEmpty(message = "DNA sequence cannot be empty")
    @ValidDnaSequence
    @Schema(
        description = "Array of DNA sequences representing NxN matrix",
        example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
        required = true
    )
    private String[] dna;
}
