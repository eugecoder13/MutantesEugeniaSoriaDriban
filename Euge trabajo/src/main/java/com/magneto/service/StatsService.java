package com.magneto.service;

import com.magneto.dto.StatsResponse;
import com.magneto.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for DNA verification statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Retrieves statistics about DNA verifications.
     *
     * @return StatsResponse with counts and ratio
     */
    public StatsResponse getVerificationStats() {
        log.info("Retrieving DNA verification statistics");

        long mutantCount = dnaRecordRepository.countByIsMutant(true);
        long humanCount = dnaRecordRepository.countByIsMutant(false);

        double ratio = calculateRatio(mutantCount, humanCount);

        log.info("Stats - Mutants: {}, Humans: {}, Ratio: {}", mutantCount, humanCount, ratio);

        return new StatsResponse(mutantCount, humanCount, ratio);
    }

    /**
     * Calculates the ratio of mutant DNA to human DNA.
     *
     * @param mutantCount number of mutant DNAs
     * @param humanCount number of human DNAs
     * @return ratio, or 0.0 if no humans recorded
     */
    private double calculateRatio(long mutantCount, long humanCount) {
        if (humanCount == 0) {
            return mutantCount > 0 ? 1.0 : 0.0;
        }
        return Math.round((double) mutantCount / humanCount * 100.0) / 100.0;
    }
}
