package org.jboss.resteasy.resteasy1058;

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
public class SumValidator implements ConstraintValidator<SumConstraint, TestResource>
{  
   private int min;
   
   public void initialize(SumConstraint constraint)
   {
      min = constraint.min();
   }

   @Override
   public boolean isValid(TestResource value, ConstraintValidatorContext context)
   {
      System.out.println("entering SumValidator.isValid(): min: " + min);
      System.out.println("field: " + value.field + ", property: " + value.getProperty());
      int sum = value.field + value.getProperty();
      return min <= sum;
   }
}
