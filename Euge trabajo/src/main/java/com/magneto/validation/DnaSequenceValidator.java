package com.magneto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/**
 * Validator implementation for DNA sequences.
 * Ensures NxN matrix structure and valid nucleotide bases (A, T, C, G).
 */
public class DnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final Set<Character> VALID_NUCLEOTIDES = Set.of('A', 'T', 'C', 'G');

    @Override
    public boolean isValid(String[] dnaSequence, ConstraintValidatorContext context) {
        if (dnaSequence == null || dnaSequence.length == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DNA sequence cannot be null or empty")
                   .addConstraintViolation();
            return false;
        }

        int matrixSize = dnaSequence.length;

        for (int rowIndex = 0; rowIndex < matrixSize; rowIndex++) {
            String currentRow = dnaSequence[rowIndex];

            // Check if row is null or empty
            if (currentRow == null || currentRow.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("DNA sequence row cannot be null or empty")
                       .addConstraintViolation();
                return false;
            }

            // Validate NxN matrix structure
            if (currentRow.length() != matrixSize) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    String.format("DNA sequence must be NxN matrix. Expected %d characters per row, found %d at row %d",
                        matrixSize, currentRow.length(), rowIndex))
                       .addConstraintViolation();
                return false;
            }

            // Validate nucleotide characters
            for (int colIndex = 0; colIndex < currentRow.length(); colIndex++) {
                char nucleotide = currentRow.charAt(colIndex);
                if (!VALID_NUCLEOTIDES.contains(nucleotide)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        String.format("Invalid nucleotide '%c' at position [%d,%d]. Only A, T, C, G are allowed",
                            nucleotide, rowIndex, colIndex))
                           .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
