package org.jboss.resteasy.links;

import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.links.impl.JsonLinkDecorator;
import org.jboss.resteasy.plugins.providers.jackson.DecoratedEntityContainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER,
      ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = JsonLinkDecorator.class, target = DecoratedEntityContainer.class)
@Documented
public @interface AddJsonLinks {
}
