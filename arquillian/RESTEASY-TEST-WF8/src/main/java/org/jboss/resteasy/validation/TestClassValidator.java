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
public class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResourceWithAllViolationTypes>
{
   int length;

   public void initialize(TestClassConstraint constraintAnnotation)
   {
      length = constraintAnnotation.value();
      System.out.println(this + " length: " + length);
   }

   public boolean isValid(TestResourceWithAllViolationTypes value, ConstraintValidatorContext context)
   {
      System.out.println(this + " value: " + value);
      System.out.println(this + " value.s: " + value.s);
      System.out.println(this + " value.retrieveS(): " + value.retrieveS());
      System.out.println(this + " value.getT(): " + value.getT());
      boolean b = value.retrieveS().length() + value.getT().length() >= length;
      System.out.println("b: " + b); return b;
//      return true;
   }
}
