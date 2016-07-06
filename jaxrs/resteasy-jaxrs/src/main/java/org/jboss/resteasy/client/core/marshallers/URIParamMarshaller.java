package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * allows a user to specify the url.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 *  
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation
 * @see package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
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
