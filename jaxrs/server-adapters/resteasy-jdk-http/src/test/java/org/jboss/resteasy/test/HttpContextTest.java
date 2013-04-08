package org.jboss.resteasy.test;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpContextTest
{

   private static HttpServer httpServer;
   private static HttpContextBuilder contextBuilder;

   @BeforeClass
   public static void before() throws Exception
   {
      int port = TestPortProvider.getPort();
      httpServer = HttpServer.create(new InetSocketAddress(port), 10);
      contextBuilder = new HttpContextBuilder();
      contextBuilder.getDeployment().getActualResourceClasses().add(SimpleResource.class);
      contextBuilder.bind(httpServer);
      httpServer.start();

   }

   @AfterClass
   public static void after() throws Exception
   {
      contextBuilder.cleanup();
      httpServer.stop(0);
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      {
         ClientRequest request = new ClientRequest(generateURL("/basic"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("basic", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/basic"));
         request.body("text/plain", "basic");
         ClientResponse<?> response = request.put();
         Assert.assertEquals(204, response.getStatus());
      }
      
      {
         ClientRequest request = new ClientRequest(generateURL("/queryParam"));
         request.queryParameter("param", "hello world");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("hello world", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/uriParam/1234"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("1234", response.getEntity());         
      }
   }

}