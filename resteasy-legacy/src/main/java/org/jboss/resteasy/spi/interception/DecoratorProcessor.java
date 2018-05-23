package org.jboss.resteasy.spi.interception;

import java.lang.annotation.Annotation;

/**
 * Part of a generic decorator framework.
 * <p>
 * Decorate a target.  For example, decorate a JAXB Marshaller with property values.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.spi.DecoratorProcessor instead.
 */
@Deprecated
public interface DecoratorProcessor<T, A extends Annotation> extends org.jboss.resteasy.spi.DecoratorProcessor<T, A>
{
}
