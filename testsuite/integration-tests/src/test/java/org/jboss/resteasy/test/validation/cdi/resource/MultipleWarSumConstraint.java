package org.jboss.resteasy.test.validation.cdi.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = MultipleWarSumValidator.class)
public @interface MultipleWarSumConstraint {
    String message() default "{org.jboss.resteasy.resteasy1058.MultipleWarSumConstraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;
}
