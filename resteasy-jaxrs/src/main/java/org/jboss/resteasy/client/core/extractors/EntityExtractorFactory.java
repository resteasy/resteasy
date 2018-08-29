package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;

/**
 * Create an EntityExtractor based on a method. This will allow different
 * factories to be used for different purposes, including custom user driven
 * factories.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractor
 * @see DefaultEntityExtractorFactory
 * @see ResponseObjectEntityExtractorFactory
 */
public interface EntityExtractorFactory
{
   @SuppressWarnings("unchecked")
   EntityExtractor createExtractor(Method method);
}
