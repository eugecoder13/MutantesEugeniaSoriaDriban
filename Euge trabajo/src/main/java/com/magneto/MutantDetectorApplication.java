package com.magneto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Mutant Detection API.
 *
 * @author Magneto
 * @version 1.0.0
 */
@SpringBootApplication
public class MutantDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutantDetectorApplication.class, args);
    }
}
