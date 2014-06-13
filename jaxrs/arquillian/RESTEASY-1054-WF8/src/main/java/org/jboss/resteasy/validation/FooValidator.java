package org.jboss.resteasy.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 18, 2013
 */
public class FooValidator implements ConstraintValidator<FooConstraint, Foo>
{
   int min;
   int max;

   public void initialize(FooConstraint constraintAnnotation)
   {
      min = constraintAnnotation.min();
      max = constraintAnnotation.max();
   }

   public boolean isValid(Foo value, ConstraintValidatorContext context)
   {
      return min <= value.s.length() && value.s.length() <= max;
   }
}