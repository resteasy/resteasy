package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.logging.Logger;

public class ValidationClassValidator implements ConstraintValidator<ValidationClassConstraint, ValidationResourceWithAllViolationTypes> {

    private static Logger logger = Logger.getLogger(ValidationClassValidator.class.getName());
    int length;

    public void initialize(ValidationClassConstraint constraintAnnotation) {
        length = constraintAnnotation.value();
        logger.info(this + " length: " + length);
    }

    public boolean isValid(ValidationResourceWithAllViolationTypes value, ConstraintValidatorContext context) {
        logger.info(this + " value: " + value);
        logger.info(this + " value.s: " + value.s);
        logger.info(this + " value.retrieveS(): " + value.retrieveS());
        logger.info(this + " value.getT(): " + value.getT());
        boolean b = value.retrieveS().length() + value.getT().length() >= length;
        logger.info("b: " + b);
        return b;
    }
}
