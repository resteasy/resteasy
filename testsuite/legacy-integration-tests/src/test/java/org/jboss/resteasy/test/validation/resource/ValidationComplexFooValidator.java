package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationComplexFooValidator implements ConstraintValidator<ValidationComplexFooConstraint, ValidationComplexFoo> {
    int min;
    int max;

    public void initialize(ValidationComplexFooConstraint constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    public boolean isValid(ValidationComplexFoo value, ConstraintValidatorContext context) {
        return min <= value.s.length() && value.s.length() <= max;
    }
}
