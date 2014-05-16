package org.jboss.resteasy.resteasy1058;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = SumValidator.class)
public @interface SumConstraint
{
   String message() default "{org.jboss.resteasy.ejb.validation.SumConstraint}";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
   int min() default 0;
}
