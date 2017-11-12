package org.jboss.resteasy.test.validation.resource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidationExceptionCrazyValidator.class)
@Target({FIELD, METHOD, PARAMETER, TYPE})
@Retention(RUNTIME)
public @interface ValidationExceptionCrazyConstraint {
	
	String DEFAULT_MESSAGE="a[][]][][b";
	
    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "";
}
