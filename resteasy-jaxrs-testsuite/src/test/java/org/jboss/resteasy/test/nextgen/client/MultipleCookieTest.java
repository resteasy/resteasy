package org.jboss.resteasy.test.nextgen.client;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * RESTEASY-1266
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class MultipleCookieTest
{
   private static final Logger log = Logger.getLogger(MultipleCookieTest.class);
   private static final String DUMMY_URL = "http://localhost:8080/404URL/";

   @Provider
   public class ContextProvider implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext context) throws IOException
      {
         try
         {
            List<Object> cookies = context.getHeaders().get(HttpHeaders.COOKIE);
            StringBuilder sb = new StringBuilder();
            for (Object o : cookies)
            {
               sb.append(((Cookie) o).toString());
            }
            Response r = Response.ok(sb.toString()).build();
            context.abortWith(r);
         }
         catch (Throwable e)
         {
            throw new IOException(e);
         }
      }
   }

   @Test
   public void multipleCookiesTest() throws Throwable
   {
      Cookie jaxrs = new Cookie("jaxrs", "jaxrs");
      Cookie jee = new Cookie("jee", "jee");
      Cookie resteasy = new Cookie("resteasy", "resteasy");
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(DUMMY_URL).register(new ContextProvider());
      Response response = target.request().cookie(jaxrs).cookie(jee).cookie(resteasy).get(Response.class);
      String entity = response.readEntity(String.class);
      log.info("entity: " + entity);
      Assert.assertTrue(entity.contains("jaxrs"));
      Assert.assertTrue(entity.contains("jee"));
      Assert.assertTrue(entity.contains("resteasy"));
   }
}
