package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
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
