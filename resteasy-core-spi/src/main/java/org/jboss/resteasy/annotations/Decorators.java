package org.jboss.resteasy.annotations;

import org.jboss.resteasy.spi.DecoratorProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation to be placed on another annotation that triggers decoration
 *
 * @see DecoratorProcessor
 * @see DecorateTypes
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorators
{
   Decorator[] values();
}
