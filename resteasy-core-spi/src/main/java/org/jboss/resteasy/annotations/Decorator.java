package org.jboss.resteasy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.resteasy.spi.DecoratorProcessor;

/**
 * Meta-annotation to be placed on another annotation that triggers decoration
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.spi.DecoratorProcessor
 * @see org.jboss.resteasy.annotations.DecorateTypes
 */
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorator {
    Class<? extends DecoratorProcessor> processor();

    Class<?> target();
}
