package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;

/**
 * Create an EntityExtractor based on a method. This will allow different
 * factories to be used for different purposes, including custom user driven
 * factories.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractor, DefaultObjectEntityExtractor,
 *      ResponseObjectEntityExtractor
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by
 *             the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractorFactory
 */
@Deprecated
public interface EntityExtractorFactory
{
   @SuppressWarnings("unchecked")
   public EntityExtractor createExtractor(Method method);
}
