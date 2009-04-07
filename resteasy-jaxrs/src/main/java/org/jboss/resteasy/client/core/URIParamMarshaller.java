package org.jboss.resteasy.client.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.resteasy.client.ClientRequest;

public class URIParamMarshaller implements Marshaller
{
   public void build(ClientRequest request, Object target)
   {
      URI uri = getUri(target);

      if (uri != null)
      {
         request.overrideUri(uri);
      }
   }

   private URI getUri(Object target)
   {
      try
      {
         if (target instanceof URI)
         {
            return (URI) target;
         }
         else if (target instanceof URL)
         {
            return ((URL) target).toURI();
         }
         else if (target instanceof String)
         {
            return new URI(target.toString());
         }
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
      return null;
   }
}
