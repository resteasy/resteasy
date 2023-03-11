package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ViolationExceptionMinMaxValidator
        implements ConstraintValidator<ViolationExceptionConstraint, ViolationExceptionObject> {
    int min;
    int max;

    public void initialize(ViolationExceptionConstraint constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    public boolean isValid(ViolationExceptionObject value, ConstraintValidatorContext context) {
        return min <= value.s.length() && value.s.length() <= max;
    }
}
