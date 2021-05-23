package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
