package org.jboss.resteasy.test.validation.resource;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidationCounterValidator.class)
@Target({ TYPE, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface ValidationCounterConstraint {
    String message() default "Validation executed more than once";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
