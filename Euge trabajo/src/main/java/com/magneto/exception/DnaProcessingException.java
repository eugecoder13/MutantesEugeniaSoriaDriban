package com.magneto.exception;

/**
 * Custom exception for DNA processing errors.
 */
public class DnaProcessingException extends RuntimeException {

    public DnaProcessingException(String message) {
        super(message);
    }

    public DnaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
