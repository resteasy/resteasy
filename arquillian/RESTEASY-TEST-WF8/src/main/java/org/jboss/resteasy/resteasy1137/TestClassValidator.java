package org.jboss.resteasy.resteasy1137;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResource>
{
   int length;

   public void initialize(TestClassConstraint constraintAnnotation)
   {
      length = constraintAnnotation.value();
   }

   public boolean isValid(TestResource value, ConstraintValidatorContext context)
   {
      boolean b = value.retrieveS().length() + value.getT().length() >= length;
      return b;
   }
}
