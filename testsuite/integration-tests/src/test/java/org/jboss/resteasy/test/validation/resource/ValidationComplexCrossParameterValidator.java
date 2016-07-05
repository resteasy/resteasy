package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ValidationComplexCrossParameterValidator implements ConstraintValidator<ValidationComplexCrossParameterConstraint, Object[]> {
    private ValidationComplexCrossParameterConstraint constraintAnnotation;

    @Override
    public void initialize(ValidationComplexCrossParameterConstraint constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        int sum = 0;

        for (int i = 0; i < value.length; i++) {
            if (!(value[i] instanceof Integer)) {
                return false;
            }
            sum += Integer.class.cast(value[i]);
        }
        return sum <= constraintAnnotation.value();
    }
}
