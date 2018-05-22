/**
 *
 */
package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

import javax.ws.rs.core.GenericType;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

/**
 * BodyEntityExtractor extract body objects from responses. This ends up calling
 * the appropriate MessageBodyReader through a series of calls.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractorFactory
 * @see javax.ws.rs.ext.MessageBodyReader
 */
@SuppressWarnings("unchecked")
public class BodyEntityExtractor implements EntityExtractor
{
   private final Method method;

   public BodyEntityExtractor(Method method)
   {
      this.method = method;
   }

   public Object extractEntity(ClientContext context, Object... args)
   {
      ClientResponse response = context.getClientResponse();

      // only release connection if it is not an instance of an
      // InputStream
      boolean releaseConnectionAfter = response.getStatus() >=200 && response.getStatus() < 300;
      try
      {
         // void methods should be handled before this method gets called, but it's worth being defensive   
         if (method.getReturnType() == null)
         {
            throw new RuntimeException(Messages.MESSAGES.noTypeInformation());
         }
         GenericType gt = null;
         if (method.getGenericReturnType() != null && !(method.getGenericReturnType() instanceof TypeVariable))
         {
            gt = new GenericType(method.getGenericReturnType());
         }
         else
         {
            gt = new GenericType(method.getReturnType());
         }
         Object obj = ClientInvocation.extractResult(gt, response, method.getAnnotations());
         if (obj instanceof InputStream || obj instanceof Reader)
            releaseConnectionAfter = false;
         return obj;
      }
      finally
      {
         if (releaseConnectionAfter)
            response.close();
      }
   }
}