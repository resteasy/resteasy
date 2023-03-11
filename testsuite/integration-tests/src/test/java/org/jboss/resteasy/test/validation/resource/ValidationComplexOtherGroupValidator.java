package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidationComplexOtherGroupValidator
        implements ConstraintValidator<ValidationComplexOtherGroupConstraint, ValidationComplexResourceWithOtherGroups> {
    public void initialize(ValidationComplexOtherGroupConstraint constraintAnnotation) {
        // nothing to do
    }

    public boolean isValid(ValidationComplexResourceWithOtherGroups value, ConstraintValidatorContext context) {
        // we need to just ensure, that RESTEasy && Bean-Validation integration can handle Bean-Validation-Groups
        return true;
    }

}
