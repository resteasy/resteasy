package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jboss.logging.Logger;

public class ValidationCounterValidator
        implements ConstraintValidator<ValidationCounterConstraint, ValidationCounter> {

    private static Logger logger = Logger.getLogger(ValidationCounterValidator.class);

    @Override
    public boolean isValid(ValidationCounter value, ConstraintValidatorContext context) {
        logger.infof("Validation executed %d time(s) so far", value.count);
        return value.count++ == 0;
    }
}
