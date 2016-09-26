package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * Must be used in conjunction with GZIPDecodingInterceptor
 * <p/>
 * Sets
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 * 
 */
@Deprecated
@Provider
@ClientInterceptor
@HeaderDecoratorPrecedence
public class AcceptEncodingGZIPInterceptor implements ClientExecutionInterceptor
{
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      LogMessages.LOGGER.debugf("Interceptor : %s, Method : execute", getClass().getName());

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
