package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Must be used in conjunction with GZIPDecodingInterceptor
 * <p>
 * Sets
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class AcceptEncodingGZIPFilter implements ClientRequestFilter
{

   @Override
   public void filter(ClientRequestContext ctx) throws IOException
   {
      String encoding = ctx.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
      if (encoding == null)
      {
         ctx.getHeaders().add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
      }
      else
      {
         if (!encoding.contains("gzip"))
         {
            encoding += ", gzip";
            ctx.getHeaders().add(HttpHeaders.ACCEPT_ENCODING, encoding);
         }
      }
   }
}
