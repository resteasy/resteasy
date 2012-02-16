/**
 *
 */
package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.BaseClientResponse;

import javax.ws.rs.ext.MessageBodyReader;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * BodyEntityExtractor extract body objects from responses. This ends up calling
 * the appropriate MessageBodyReader through a series of calls
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractorFactory
 * @see MessageBodyReader
 */
@SuppressWarnings("unchecked")
public class BodyEntityExtractor implements EntityExtractor
{
   private final Method method;

   public BodyEntityExtractor(Method method)
   {
      this.method = method;
   }

   public Object extractEntity(ClientRequestContext context, Object... args)
   {
      final BaseClientResponse response = context.getClientResponse();
      try
      {
         response.checkFailureStatus();
      }
      catch (ClientResponseFailure ce)
      {
         // If ClientResponseFailure do a copy of the response and then release the connection,
         // we need to use the copy here and not the original response
         context.getErrorHandler().clientErrorHandling((BaseClientResponse) ce.getResponse(), ce);
      }
      catch (RuntimeException e)
      {
         context.getErrorHandler().clientErrorHandling(response, e);
      }

      // only release connection if it is not an instance of an
      // InputStream
      boolean releaseConnectionAfter = true;
      try
      {
         // void methods should be handled before this method gets called, but it's worth being defensive   
         if (method.getReturnType() == null)
         {
            throw new RuntimeException(
                    "No type information to extract entity with.  You use other getEntity() methods");
         }
         Object obj = response.getEntity(method.getReturnType(), method.getGenericReturnType());
         if (obj instanceof InputStream)
            releaseConnectionAfter = false;
         return obj;
      }
      catch (RuntimeException e)
      {
         context.getErrorHandler().clientErrorHandling(response, e);
      }
      finally
      {
         if (releaseConnectionAfter)
            response.releaseConnection();
      }
      throw new RuntimeException("Should be unreachable");
   }
}