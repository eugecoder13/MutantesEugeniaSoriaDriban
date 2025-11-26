package com.magneto.service;

import com.magneto.entity.DnaRecord;
import com.magneto.exception.DnaProcessingException;
import com.magneto.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Service for mutant DNA detection and persistence.
 * Implements SHA-256 hashing for deduplication strategy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Analyzes a DNA sequence and persists the result.
     * Uses hash-based deduplication to avoid duplicate records.
     *
     * @param dnaSequence array of strings representing DNA matrix
     * @return true if mutant, false if human
     */
    @Transactional
    public boolean analyzeDna(String[] dnaSequence) {
        log.info("Analyzing DNA sequence of size: {}", dnaSequence.length);

        // Calculate hash for deduplication
        String dnaHash = calculateDnaHash(dnaSequence);
        log.debug("Calculated DNA hash: {}", dnaHash);

        // Check if already analyzed
        return dnaRecordRepository.findByDnaHash(dnaHash)
                .map(existingRecord -> {
                    log.info("DNA already analyzed. Result from cache: isMutant={}", existingRecord.getIsMutant());
                    return existingRecord.getIsMutant();
                })
                .orElseGet(() -> {
                    // Perform analysis
                    boolean isMutant = mutantDetector.isMutant(dnaSequence);
                    log.info("New DNA analyzed. Result: isMutant={}", isMutant);

                    // Save result
                    DnaRecord newRecord = new DnaRecord();
                    newRecord.setDnaHash(dnaHash);
                    newRecord.setIsMutant(isMutant);
                    newRecord.setVerifiedAt(LocalDateTime.now());

                    dnaRecordRepository.save(newRecord);
                    log.debug("DNA record saved with hash: {}", dnaHash);

                    return isMutant;
                });
    }

    /**
     * Calculates SHA-256 hash of the DNA sequence.
     * Normalizes the DNA array to ensure consistent hashing.
     *
     * @param dnaSequence DNA array
     * @return SHA-256 hash in hexadecimal format
     */
    private String calculateDnaHash(String[] dnaSequence) {
        try {
            // Normalize: sort and join to ensure consistent hash for same DNA
            String normalizedDna = String.join("", Arrays.asList(dnaSequence));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(normalizedDna.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new DnaProcessingException("Failed to calculate DNA hash", e);
        }
    }
}
