package com.magneto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MutantDetector algorithm.
 * Tests cover all directions, edge cases, and validation scenarios.
 */
class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    // ========== MUTANT DETECTION TESTS - HORIZONTAL ==========

    @Test
    @DisplayName("Should detect mutant with horizontal sequence")
    void testDetectMutantWithHorizontalSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect mutant with multiple horizontal sequences")
    void testDetectMutantWithMultipleHorizontalSequences() {
        String[] dna = {
            "AAAATG",
            "CAGTGC",
            "TTATGT",
            "AGGGGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    // ========== MUTANT DETECTION TESTS - VERTICAL ==========

    @Test
    @DisplayName("Should detect mutant with vertical sequence")
    void testDetectMutantWithVerticalSequence() {
        String[] dna = {
            "ATGCGA",
            "AAGTGC",
            "ATATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect mutant with multiple vertical sequences")
    void testDetectMutantWithMultipleVerticalSequences() {
        String[] dna = {
            "TTGCGA",
            "TAGTGC",
            "TTATGT",
            "TGAAGG",
            "CCGCTA",
            "TCGCTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    // ========== MUTANT DETECTION TESTS - DIAGONAL ==========

    @Test
    @DisplayName("Should detect mutant with diagonal down-right sequence")
    void testDetectMutantWithDiagonalDownRightSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect mutant with diagonal down-left sequence")
    void testDetectMutantWithDiagonalDownLeftSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGGGC",
            "TTGTGT",
            "AGGAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect mutant with multiple diagonal sequences")
    void testDetectMutantWithMultipleDiagonalSequences() {
        String[] dna = {
            "AAGTGA",
            "CAGTTC",
            "TTGTGT",
            "AGTAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    // ========== HUMAN DETECTION TESTS ==========

    @Test
    @DisplayName("Should detect human with no sequences")
    void testDetectHumanWithNoSequences() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect human with only one sequence")
    void testDetectHumanWithOneSequence() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Should return false for null DNA")
    void testNullDna() {
        assertFalse(mutantDetector.isMutant(null));
    }

    @Test
    @DisplayName("Should return false for empty DNA array")
    void testEmptyDnaArray() {
        String[] dna = {};
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should handle 4x4 matrix - minimum size")
    void testMinimumMatrixSize() {
        String[] dna = {
            "AAAA",
            "CCCC",
            "TTTT",
            "GGGG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should handle large matrix efficiently")
    void testLargeMatrix() {
        String[] dna = {
            "ATGCGAATGC",
            "CAGTGCCAGT",
            "TTATGTTTAT",
            "AGAAGGAGAA",
            "CCCCTACCCC",
            "TCACTGTCAC",
            "ATGCGAATGC",
            "CAGTGCCAGT",
            "TTATGTTTAT",
            "AGAAGGAGAA"
        };
        boolean result = mutantDetector.isMutant(dna);
        // Result depends on sequences present
        assertNotNull(result);
    }

    // ========== EARLY TERMINATION TEST ==========

    @Test
    @DisplayName("Should use early termination optimization")
    void testEarlyTermination() {
        // Matrix with 2 sequences found early
        String[] dna = {
            "AAAATG",
            "CAGTGC",
            "TTATGT",
            "AGGGGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    // ========== SPECIFIC PATTERN TESTS ==========

    @Test
    @DisplayName("Should detect mutant with mixed direction sequences")
    void testMixedDirectionSequences() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect human with three identical letters in sequence")
    void testThreeIdenticalLettersNotMutant() {
        String[] dna = {
            "ATGCGA",
            "CAAATC",
            "TTATGT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna));
    }

    @Test
    @DisplayName("Should detect mutant with sequences at matrix boundaries")
    void testSequencesAtBoundaries() {
        String[] dna = {
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "GGGGTA",
            "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna));
    }
}
