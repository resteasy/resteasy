package org.jboss.resteasy.cdi.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@ApplicationScoped
public class SumValidator implements ConstraintValidator<SumConstraint, ResourceParent>
{  
   private int min;
   private int max;
   
   public void initialize(SumConstraint constraint)
   {
      min = constraint.min();
      max = constraint.max();
   }
   
   public boolean isValid(ResourceParent value, ConstraintValidatorContext context)
   {
      System.out.println("entering SumValidator.isValid(): min: " + min + ", max; " + max);
      System.out.println("one: " + value.getNumberOne() + ", two: " + value.getNumberTwo());
      int sum = value.getNumberOne() + value.getNumberTwo();
      return min <= sum && sum <= max;
   }
}
