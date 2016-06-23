package org.jboss.resteasy.resteasy945;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 14, 2014
 */
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
