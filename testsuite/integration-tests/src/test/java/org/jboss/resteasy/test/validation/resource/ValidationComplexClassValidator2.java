package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidationComplexClassValidator2 implements ConstraintValidator<ValidationComplexClassConstraint2, ValidationComplexResourceWithAllFivePotentialViolations> {
   public int length;

   public void initialize(ValidationComplexClassConstraint2 constraintAnnotation) {
      length = constraintAnnotation.value();
   }

   public boolean isValid(ValidationComplexResourceWithAllFivePotentialViolations value, ConstraintValidatorContext context) {
      return value.s.length() + value.t.length() >= length;
   }

}
