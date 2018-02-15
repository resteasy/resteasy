package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationComplexClassValidator implements ConstraintValidator<ValidationComplexClassConstraint, ValidationComplexResourceWithClassConstraintInterface> {
    public int length;

    public void initialize(ValidationComplexClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ValidationComplexResourceWithClassConstraintInterface value, ConstraintValidatorContext context) {
        return value.getS().length() + value.getT().length() >= length;
    }

}
