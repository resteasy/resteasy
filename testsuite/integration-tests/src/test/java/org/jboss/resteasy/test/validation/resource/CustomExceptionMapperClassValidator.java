package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomExceptionMapperClassValidator implements ConstraintValidator<CustomExceptionMapperClassConstraint, CustomExceptionMapperResource> {
    int length;

    public void initialize(CustomExceptionMapperClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(CustomExceptionMapperResource value, ConstraintValidatorContext context) {
        boolean b = value.retrieveS().length() + value.getT().length() >= length;
        return b;
    }
}
