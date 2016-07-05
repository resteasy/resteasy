package org.jboss.resteasy.resteasy945;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

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
