package org.jboss.resteasy.test.validation.resource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidationComplexClassValidatorSubInheritance.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidationComplexClassInheritanceSubConstraint {
    String message() default "u must have value {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}
