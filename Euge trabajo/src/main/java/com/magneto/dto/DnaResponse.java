package com.magneto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for DNA verification response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DNA verification response")
public class DnaResponse {

    @Schema(description = "Indicates if the DNA belongs to a mutant", example = "true")
    private boolean isMutant;

    @Schema(description = "Additional message about the verification", example = "Mutant DNA detected successfully")
    private String message;
}
