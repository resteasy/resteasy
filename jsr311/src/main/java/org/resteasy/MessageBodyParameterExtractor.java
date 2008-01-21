package org.resteasy;

import org.resteasy.spi.HttpInput;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterExtractor implements ParameterExtractor
{
   private Class type;
   private ResteasyProviderFactory factory;

   public MessageBodyParameterExtractor(Class type, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.factory = factory;
   }

   public Object extract(HttpInput request)
   {
      try
      {
         MediaType mediaType = request.getHttpHeaders().getMediaType();
         MessageBodyReader reader = factory.createMessageBodyReader(type, mediaType);
         return reader.readFrom(type, mediaType, request.getHttpHeaders().getRequestHeaders(), request.getInputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failure extracting body", e);
      }
   }
}
