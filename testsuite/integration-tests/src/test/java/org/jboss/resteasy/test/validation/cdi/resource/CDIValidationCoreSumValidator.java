package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@ApplicationScoped
public class CDIValidationCoreSumValidator implements ConstraintValidator<CDIValidationCoreSumConstraint, CDIValidationCoreResource> {
    private int min;

    public void initialize(CDIValidationCoreSumConstraint constraint) {
        min = constraint.min();
    }

    @Override
    public boolean isValid(CDIValidationCoreResource value, ConstraintValidatorContext context) {
        int sum = value.field + value.getProperty();
        return min <= sum;
    }
}
