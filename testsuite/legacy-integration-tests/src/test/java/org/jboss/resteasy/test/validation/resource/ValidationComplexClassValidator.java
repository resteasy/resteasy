package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationComplexClassValidator implements ConstraintValidator<ValidationComplexClassConstraint, ValidationComplexResourceWithClassConstraint> {
    public int length;

    public void initialize(ValidationComplexClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ValidationComplexResourceWithClassConstraint value, ConstraintValidatorContext context) {
        return value.s.length() + value.t.length() >= length;
    }

}
