package org.jboss.resteasy.test;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

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
      Client client = ClientBuilder.newClient();
      
      {
         Response response = client.target(generateURL("/basic")).request().get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("basic", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/basic")).request().put(Entity.entity("basic", "text/plain"));
         Assert.assertEquals(204, response.getStatus());
         response.close();
      }
      
      {
         Response response = client.target(generateURL("/queryParam")).queryParam("param", "hello world").request().get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("hello world", response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/uriParam/1234")).request().get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("1234", response.readEntity(String.class));   
      }
   }

}