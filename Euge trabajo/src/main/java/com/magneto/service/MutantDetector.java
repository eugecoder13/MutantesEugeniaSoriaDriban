package com.magneto.service;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Core algorithm for detecting mutant DNA sequences.
 * Implements optimized detection with early termination and efficient memory usage.
 */
@Component
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Determines if a DNA sequence belongs to a mutant.
     * A mutant has more than one sequence of four identical letters
     * in horizontal, vertical, or diagonal directions.
     *
     * @param dnaSequence array of strings representing the DNA matrix
     * @return true if mutant, false if human
     */
    public boolean isMutant(String[] dnaSequence) {
        if (dnaSequence == null || dnaSequence.length == 0) {
            return false;
        }

        // Convert to char matrix for O(1) access
        char[][] dnaMatrix = convertToMatrix(dnaSequence);
        int gridSize = dnaMatrix.length;

        int sequencesFound = 0;

        // Single pass through the matrix checking all directions
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                char currentBase = dnaMatrix[row][col];

                // Check horizontal sequence (boundary check)
                if (col <= gridSize - SEQUENCE_LENGTH) {
                    if (checkHorizontalMatch(dnaMatrix, row, col, currentBase)) {
                        sequencesFound++;
                        // Early termination optimization
                        if (sequencesFound > 1) return true;
                    }
                }

                // Check vertical sequence (boundary check)
                if (row <= gridSize - SEQUENCE_LENGTH) {
                    if (checkVerticalMatch(dnaMatrix, row, col, currentBase)) {
                        sequencesFound++;
                        // Early termination optimization
                        if (sequencesFound > 1) return true;
                    }
                }

                // Check diagonal down-right (boundary check)
                if (row <= gridSize - SEQUENCE_LENGTH && col <= gridSize - SEQUENCE_LENGTH) {
                    if (checkDiagonalDownRightMatch(dnaMatrix, row, col, currentBase)) {
                        sequencesFound++;
                        // Early termination optimization
                        if (sequencesFound > 1) return true;
                    }
                }

                // Check diagonal down-left (boundary check)
                if (row <= gridSize - SEQUENCE_LENGTH && col >= SEQUENCE_LENGTH - 1) {
                    if (checkDiagonalDownLeftMatch(dnaMatrix, row, col, currentBase)) {
                        sequencesFound++;
                        // Early termination optimization
                        if (sequencesFound > 1) return true;
                    }
                }
            }
        }

        return sequencesFound > 1;
    }

    /**
     * Converts string array to char matrix for efficient access.
     *
     * @param dnaSequence string array representation
     * @return 2D char array
     */
    private char[][] convertToMatrix(String[] dnaSequence) {
        int size = dnaSequence.length;
        char[][] matrix = new char[size][];
        for (int i = 0; i < size; i++) {
            matrix[i] = dnaSequence[i].toCharArray();
        }
        return matrix;
    }

    /**
     * Checks for horizontal sequence match.
     * Direct comparison for optimal performance.
     */
    private boolean checkHorizontalMatch(char[][] matrix, int row, int col, char base) {
        return matrix[row][col + 1] == base &&
               matrix[row][col + 2] == base &&
               matrix[row][col + 3] == base;
    }

    /**
     * Checks for vertical sequence match.
     * Direct comparison for optimal performance.
     */
    private boolean checkVerticalMatch(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col] == base &&
               matrix[row + 2][col] == base &&
               matrix[row + 3][col] == base;
    }

    /**
     * Checks for diagonal down-right sequence match.
     * Direct comparison for optimal performance.
     */
    private boolean checkDiagonalDownRightMatch(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col + 1] == base &&
               matrix[row + 2][col + 2] == base &&
               matrix[row + 3][col + 3] == base;
    }

    /**
     * Checks for diagonal down-left sequence match.
     * Direct comparison for optimal performance.
     */
    private boolean checkDiagonalDownLeftMatch(char[][] matrix, int row, int col, char base) {
        return matrix[row + 1][col - 1] == base &&
               matrix[row + 2][col - 2] == base &&
               matrix[row + 3][col - 3] == base;
    }
}
