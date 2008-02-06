package org.resteasy.test.smoke;

import Acme.Serve.Serve;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestResourceWithInterface
{

   private static Serve server = null;
   private static HttpServletDispatcher dispatcher = new HttpServletDispatcher();

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      server = new Serve();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      POJOResourceFactory noDefaults = new POJOResourceFactory(ResourceWithInterface.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      SimpleClient client = ProxyFactory.create(SimpleClient.class, "http://localhost:8081");

      Assert.assertEquals("basic", client.getBasic());
      client.putBasic("hello world");
      Assert.assertEquals("hello world", client.getQueryParam("hello world"));
      Assert.assertEquals(1234, client.getUriParam(1234));


   }
}