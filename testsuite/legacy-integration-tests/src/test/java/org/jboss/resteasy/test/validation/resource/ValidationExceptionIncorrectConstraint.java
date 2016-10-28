package org.jboss.resteasy.test.validation.resource;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidationExceptionClassValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidationExceptionIncorrectConstraint {
}
