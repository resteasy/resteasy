package org.jboss.resteasy.plugins.client.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostInvoker extends HttpClientInvoker
{
   public PostInvoker(HttpClient client, Class<?> declaring, Method method, ResteasyProviderFactory providerFactory)
   {
      super(client, declaring, method, providerFactory);
   }

   public HttpMethodBase createBaseMethod(String uri)
   {
      return new PostMethod(uri);
   }
}