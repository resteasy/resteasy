package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Must be used in conjunction with GZIPDecodingInterceptor
 * <p/>
 * Sets
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@BindingPriority(BindingPriority.HEADER_DECORATOR)
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
