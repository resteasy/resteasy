package org.jboss.resteasy.validation;

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
@Constraint(validatedBy = TestClassValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface TestClassConstraint
{
   String message() default "Concatenation of s and t must have length > {value}";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
   int value();
}
