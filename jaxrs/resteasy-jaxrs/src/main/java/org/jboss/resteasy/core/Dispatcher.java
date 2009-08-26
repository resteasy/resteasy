package org.jboss.resteasy.core;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Dispatcher
{
   ResteasyProviderFactory getProviderFactory();

   Registry getRegistry();

   void setMediaTypeMappings(Map<String, MediaType> mediaTypeMappings);

   void setLanguageMappings(Map<String, String> languageMappings);

   Map<String, MediaType> getMediaTypeMappings();

   Map<String, String> getLanguageMappings();

   void invoke(HttpRequest in, HttpResponse response);
   
   Response internalInvocation(HttpRequest request, HttpResponse response, Object entity);

   void addHttpPreprocessor(HttpRequestPreprocessor httpPreprocessor);
}
