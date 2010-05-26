package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public interface EntityExtractorFactory
{
   public EntityExtractor createExtractor(Method method, ClientErrorHandler errorHandler);
}
