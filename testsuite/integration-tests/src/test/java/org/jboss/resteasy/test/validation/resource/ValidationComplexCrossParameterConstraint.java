package org.jboss.resteasy.test.validation.resource;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidationComplexCrossParameterValidator.class)
@Target({ METHOD })
@Retention(RUNTIME)
public @interface ValidationComplexCrossParameterConstraint {
    String message() default "Parameters must total <= {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
