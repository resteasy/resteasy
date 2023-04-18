package org.jboss.resteasy.test.validation.cdi.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@ApplicationScoped
public class MultipleWarSumValidator implements ConstraintValidator<MultipleWarSumConstraint, MultipleWarResource> {
    private int min;

    public void initialize(MultipleWarSumConstraint constraint) {
        min = constraint.min();
    }

    @Override
    public boolean isValid(MultipleWarResource value, ConstraintValidatorContext context) {
        int sum = value.field + value.getProperty();
        return min <= sum;
    }
}
