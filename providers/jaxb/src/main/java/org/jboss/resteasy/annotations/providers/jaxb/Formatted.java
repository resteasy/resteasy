package org.jboss.resteasy.annotations.providers.jaxb;

import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.plugins.providers.jaxb.PrettyProcessor;

import javax.xml.bind.Marshaller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Format XML output with indentations and newlines.
 * <p>
 * This is a JAXB Decorator
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = PrettyProcessor.class, target = Marshaller.class)
public @interface Formatted
{
}
