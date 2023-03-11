package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidationComplexClassValidatorSuperInheritance
        implements ConstraintValidator<ValidationComplexClassInheritanceSuperConstraint, ValidationComplexInterfaceSuper> {
    int length;

    public void initialize(ValidationComplexClassInheritanceSuperConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ValidationComplexInterfaceSuper value, ConstraintValidatorContext context) {
        return value.t.length() >= length;
    }
}
