package org.jboss.resteasy.test.validation.resource;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ValidationComplexClassValidatorSubInheritance.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidationComplexClassInheritanceSubConstraint {
    String message() default "u must have value {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}
