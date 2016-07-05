package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationCoreFooValidator implements ConstraintValidator<ValidationCoreFooConstraint, ValidationCoreFoo> {
    int min;
    int max;

    public void initialize(ValidationCoreFooConstraint constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    public boolean isValid(ValidationCoreFoo value, ConstraintValidatorContext context) {
        return min <= value.s.length() && value.s.length() <= max;
    }
}