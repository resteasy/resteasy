package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jboss.logging.Logger;

public class ValidationComplexClassValidatorSubInheritance
        implements ConstraintValidator<ValidationComplexClassInheritanceSubConstraint, ValidationComplexInterfaceSub> {
    private static Logger logger = Logger.getLogger(ValidationComplexClassValidatorSubInheritance.class);
    String pattern;

    public void initialize(ValidationComplexClassInheritanceSubConstraint constraintAnnotation) {
        pattern = constraintAnnotation.value();
    }

    public boolean isValid(ValidationComplexInterfaceSub value, ConstraintValidatorContext context) {
        logger.info(this + "u: " + value.u);
        logger.info("pattern: " + pattern + ", matches: " + value.u.matches(pattern));
        return value.u.matches(pattern);
    }
}
