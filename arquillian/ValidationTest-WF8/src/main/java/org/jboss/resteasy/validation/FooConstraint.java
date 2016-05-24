package org.jboss.resteasy.validation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 18, 2013
 */
@Documented
@Constraint(validatedBy = FooValidator.class)
@Target({ TYPE, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface FooConstraint
{
   String message() default "s must have length: {min} <= length <= {max}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};

   int min();

   int max();
}
