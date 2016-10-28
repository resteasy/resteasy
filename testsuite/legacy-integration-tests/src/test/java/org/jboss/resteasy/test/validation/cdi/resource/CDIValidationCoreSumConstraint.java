package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = CDIValidationCoreSumValidator.class)
public @interface CDIValidationCoreSumConstraint {
    String message() default "{org.jboss.resteasy.ejb.validation.SumConstraint}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;
}
