package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationExceptionCrazyValidator implements ConstraintValidator<ValidationExceptionCrazyConstraint, ValidationExceptionResourceCrazy> {
    ValidationExceptionCrazyConstraint constraint;

    @Override
    public void initialize(ValidationExceptionCrazyConstraint constraintAnnotation) {
        constraint = constraintAnnotation;
    }

    @Override
    public boolean isValid(ValidationExceptionResourceCrazy r, ConstraintValidatorContext context) {
        return false;
    }
}
