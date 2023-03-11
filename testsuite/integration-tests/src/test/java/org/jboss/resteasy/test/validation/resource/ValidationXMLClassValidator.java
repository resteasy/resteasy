package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidationXMLClassValidator
        implements ConstraintValidator<ValidationXMLClassConstraint, ValidationXMLResourceWithAllFivePotentialViolations> {
    int length;

    public void initialize(ValidationXMLClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ValidationXMLResourceWithAllFivePotentialViolations value, ConstraintValidatorContext context) {
        return value.s.length() + value.u.length() >= length;
    }

}
