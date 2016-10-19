package org.jboss.resteasy.core.interception;

/**
 * Finds DecoratorProcessors and calls decorates on them by introspecting annotations.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.spi.DecoratorProcessor
 * @see org.jboss.resteasy.annotations.DecorateTypes
 * @see org.jboss.resteasy.annotations.Decorator
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.DecoratorMatcher instead.
 */
@Deprecated
public class DecoratorMatcher extends org.jboss.resteasy.core.interception.jaxrs.DecoratorMatcher
{
}
