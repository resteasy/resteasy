package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PathSuppressionClassValidator
        implements ConstraintValidator<PathSuppressionClassConstraint, PathSuppressionResource> {
    int length;

    public void initialize(PathSuppressionClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    public boolean isValid(PathSuppressionResource value, ConstraintValidatorContext context) {
        boolean b = value.retrieveS().length() + value.getT().length() >= length;
        return b;
    }
}
