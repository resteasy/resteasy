package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.interception.ClientExecutionContext;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.core.interception.ClientInterceptor;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * Must be used in conjunction with GZIPDecodingInterceptor
 * <p/>
 * Sets
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ClientInterceptor
public class AcceptEncodingGZIPInterceptor implements ClientExecutionInterceptor
{
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      String encoding = ctx.getRequest().getHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);
      if (encoding == null)
      {
         ctx.getRequest().header(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
      }
      else
      {
         if (!encoding.contains("gzip"))
         {
            encoding += ", gzip";
            ctx.getRequest().header(HttpHeaders.ACCEPT_ENCODING, encoding);
         }
      }
      return ctx.proceed();
   }
}
