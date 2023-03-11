package org.jboss.resteasy.test.validation.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidationOnGetterNotNullOrOneStringBeanValidator.class)
public @interface ValidationOnGetterNotNullOrOne {
    String message() default "{ValidationOnGetterNotNullOrOne.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
