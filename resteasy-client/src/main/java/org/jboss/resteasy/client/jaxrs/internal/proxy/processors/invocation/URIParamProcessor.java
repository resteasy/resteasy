package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * allows a user to specify the url.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public class URIParamProcessor implements InvocationProcessor
{

   @Override
   public void process(ClientInvocation invocation, Object param)
   {
      URI uri = getUri(param);

      if (uri != null)
      {
         invocation.setUri(uri);
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
