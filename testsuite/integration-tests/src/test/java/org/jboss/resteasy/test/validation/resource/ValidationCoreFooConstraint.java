package org.jboss.resteasy.test.validation.resource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidationCoreFooValidator.class)
@Target({TYPE, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface ValidationCoreFooConstraint {
    String message() default "s must have length: {min} <= length <= {max}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();

    int max();
}
