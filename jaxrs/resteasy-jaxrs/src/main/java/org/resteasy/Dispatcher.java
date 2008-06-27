package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import java.util.Map;

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
}
