package com.magneto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a DNA verification record in the database.
 * Uses SHA-256 hash for deduplication strategy.
 */
@Entity
@Table(name = "dna_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dna_hash", unique = true, nullable = false, length = 64)
    private String dnaHash;

    @Column(name = "is_mutant", nullable = false)
    private Boolean isMutant;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        if (verifiedAt == null) {
            verifiedAt = LocalDateTime.now();
        }
    }
}
