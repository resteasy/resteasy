package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationCoreClassValidator implements ConstraintValidator<ValidationCoreClassConstraint, ValidationCoreResourceWithAllViolationTypes> {
    int length;

    public void initialize(ValidationCoreClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ValidationCoreResourceWithAllViolationTypes value, ConstraintValidatorContext context) {
        boolean b = value.retrieveS().length() + value.getT().length() >= length;
        return b;
    }
}
