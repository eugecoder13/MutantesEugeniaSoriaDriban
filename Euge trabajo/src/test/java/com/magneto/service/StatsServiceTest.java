package com.magneto.service;

import com.magneto.dto.StatsResponse;
import com.magneto.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StatsService.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("Should calculate statistics with mutants and humans")
    void testGetStatsWithMutantsAndHumans() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(40L, stats.getCountMutantDna());
        assertEquals(100L, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.01);

        verify(dnaRecordRepository, times(1)).countByIsMutant(true);
        verify(dnaRecordRepository, times(1)).countByIsMutant(false);
    }

    @Test
    @DisplayName("Should handle zero humans in statistics")
    void testGetStatsWithNoHumans() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(10L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio(), 0.01);
    }

    @Test
    @DisplayName("Should handle zero mutants in statistics")
    void testGetStatsWithNoMutants() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(50L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.01);
    }

    @Test
    @DisplayName("Should handle empty database")
    void testGetStatsWithEmptyDatabase() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.01);
    }

    @Test
    @DisplayName("Should calculate ratio correctly with equal counts")
    void testGetStatsWithEqualCounts() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(50L, stats.getCountMutantDna());
        assertEquals(50L, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio(), 0.01);
    }

    @Test
    @DisplayName("Should round ratio to 2 decimal places")
    void testRatioRounding() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(3L);

        StatsResponse stats = statsService.getVerificationStats();

        assertEquals(0.33, stats.getRatio(), 0.01);
    }
}
