package org.jboss.resteasy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.resteasy.spi.DecoratorProcessor;

/**
 * Meta-annotation to be placed on another annotation that triggers decoration
 *
 * @see DecoratorProcessor
 * @see DecorateTypes
 */
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorators {
    Decorator[] values();
}
