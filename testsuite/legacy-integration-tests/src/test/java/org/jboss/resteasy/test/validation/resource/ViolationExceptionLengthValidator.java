package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ViolationExceptionLengthValidator implements ConstraintValidator<ViolationExceptionLengthConstraint, ViolationExceptionResourceWithFiveViolations> {
    int length;

    public void initialize(ViolationExceptionLengthConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(ViolationExceptionResourceWithFiveViolations value, ConstraintValidatorContext context) {
        return value.s.length() + value.t.length() >= length;
    }

}
