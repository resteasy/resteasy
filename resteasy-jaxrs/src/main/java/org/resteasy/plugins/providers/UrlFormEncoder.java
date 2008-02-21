package org.resteasy.plugins.providers;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ConsumeMime("application/x-www-form-urlencoded")
public class UrlFormEncoder implements MessageBodyReader<MultivaluedMap<String, String>>
{
   public boolean isReadable(Class<?> type)
   {
      return (MultivaluedMap.class.isAssignableFrom(type));
   }

   public MultivaluedMap<String, String> readFrom(Class<MultivaluedMap<String, String>> type, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }
}
