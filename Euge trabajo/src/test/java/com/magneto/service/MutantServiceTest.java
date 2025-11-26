package com.magneto.service;

import com.magneto.entity.DnaRecord;
import com.magneto.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MutantService.
 */
@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATGT",
            "AGAAGG",
            "CCCCTA",
            "TCACTG"
        };

        humanDna = new String[]{
            "ATGCGA",
            "CAGTGC",
            "TTATTT",
            "AGACGG",
            "GCGTCA",
            "TCACTG"
        };
    }

    @Test
    @DisplayName("Should analyze new mutant DNA and save to database")
    void testAnalyzeNewMutantDna() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        boolean result = mutantService.analyzeDna(mutantDna);

        assertTrue(result);
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Should analyze new human DNA and save to database")
    void testAnalyzeNewHumanDna() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        boolean result = mutantService.analyzeDna(humanDna);

        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Should return cached result for previously analyzed mutant DNA")
    void testAnalyzeCachedMutantDna() {
        DnaRecord cachedRecord = new DnaRecord();
        cachedRecord.setIsMutant(true);

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        boolean result = mutantService.analyzeDna(mutantDna);

        assertTrue(result);
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return cached result for previously analyzed human DNA")
    void testAnalyzeCachedHumanDna() {
        DnaRecord cachedRecord = new DnaRecord();
        cachedRecord.setIsMutant(false);

        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        boolean result = mutantService.analyzeDna(humanDna);

        assertFalse(result);
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should generate consistent hash for same DNA sequence")
    void testConsistentHashGeneration() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        mutantService.analyzeDna(mutantDna);
        mutantService.analyzeDna(mutantDna);

        // Should find cached result on second call
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
    }
}
