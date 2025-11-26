package com.magneto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for DNA sequences.
 * Validates that the DNA sequence is a valid NxN matrix with only A, T, C, G characters.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DnaSequenceValidator.class)
public @interface ValidDnaSequence {

    String message() default "Invalid DNA sequence format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
