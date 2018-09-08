package org.jboss.resteasy.spi;

import java.util.Map;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Dispatcher
{
   ResteasyProviderFactory getProviderFactory();

   Registry getRegistry();

   void invoke(HttpRequest in, HttpResponse response);

   Response internalInvocation(HttpRequest request, HttpResponse response, Object entity);

   void addHttpPreprocessor(HttpRequestPreprocessor httpPreprocessor);

   Map<Class, Object> getDefaultContextObjects();
}
