package org.jboss.resteasy.test.validation.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationOnGetterNotNullOrOneStringBeanValidator implements ConstraintValidator<ValidationOnGetterNotNullOrOne, ValidationOnGetterStringBean> {
    @Override
    public void initialize(ValidationOnGetterNotNullOrOne arg0) {
    }

    @Override
    public boolean isValid(ValidationOnGetterStringBean bean, ConstraintValidatorContext context) {
        String value = bean.get();
        if (value == null || value.length() < 2) {
            return false;
        }
        return true;
    }
}
