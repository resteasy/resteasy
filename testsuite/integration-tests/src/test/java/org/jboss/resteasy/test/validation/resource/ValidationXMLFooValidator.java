package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationXMLFooValidator implements ConstraintValidator<ValidationXMLFooConstraint, ValidationXMLFoo> {
    int min;
    int max;

    public void initialize(ValidationXMLFooConstraint constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    public boolean isValid(ValidationXMLFoo value, ConstraintValidatorContext context) {
        return min <= value.s.length() && value.s.length() <= max;
    }
}
