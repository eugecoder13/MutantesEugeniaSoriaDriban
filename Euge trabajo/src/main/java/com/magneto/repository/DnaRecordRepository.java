package com.magneto.repository;

import com.magneto.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for DNA record persistence operations.
 */
@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Find a DNA record by its hash.
     *
     * @param dnaHash SHA-256 hash of the DNA sequence
     * @return Optional containing the DNA record if found
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Count DNA records by mutant status.
     *
     * @param isMutant true to count mutants, false to count humans
     * @return count of records matching the status
     */
    long countByIsMutant(Boolean isMutant);
}
