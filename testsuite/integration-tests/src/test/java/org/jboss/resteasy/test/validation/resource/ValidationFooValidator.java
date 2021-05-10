package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidationFooValidator implements ConstraintValidator<ValidationFooConstraint, ValidationFoo> {
   int min;
   int max;

   public void initialize(ValidationFooConstraint constraintAnnotation) {
      min = constraintAnnotation.min();
      max = constraintAnnotation.max();
   }

   public boolean isValid(ValidationFoo value, ConstraintValidatorContext context) {
      return min <= value.s.length() && value.s.length() <= max;
   }
}
