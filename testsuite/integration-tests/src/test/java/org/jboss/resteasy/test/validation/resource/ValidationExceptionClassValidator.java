package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationExceptionClassValidator implements ConstraintValidator<ValidationExceptionIncorrectConstraint, ValidationExceptionResourceWithIncorrectConstraint> {
    int length;

    public void initialize(ValidationExceptionIncorrectConstraint constraintAnnotation) {
    }

    public boolean isValid(ValidationExceptionResourceWithIncorrectConstraint value, ConstraintValidatorContext context) {
        return true;
    }
}
