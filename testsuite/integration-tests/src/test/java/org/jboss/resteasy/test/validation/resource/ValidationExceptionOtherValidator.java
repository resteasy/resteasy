package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationExceptionOtherValidator implements ConstraintValidator<ValidationExceptionOtherConstraint, String> {
    @Override
    public void initialize(ValidationExceptionOtherConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if ("ok".equals(s)) {
            return true;
        }
        if ("fail".equals(s)) {
            throw new ValidationExceptionOtherValidationException();
        }
        throw new ValidationExceptionOtherValidationException();
    }
}
