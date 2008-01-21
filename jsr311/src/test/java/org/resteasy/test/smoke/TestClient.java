package org.resteasy.test.smoke;

import Acme.Serve.Serve;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.providers.DefaultPlainText;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.ResteasyProviderFactory;

import java.util.Properties;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestClient
{

   private static Serve server = null;
   private static HttpServletDispatcher dispatcher = new HttpServletDispatcher();

   @BeforeClass
   public static void before() throws Exception
   {
      server = new Serve();
      Properties props = new Properties();
      props.put("port", 8081);
      props.setProperty(Serve.ARG_NOHUP, "nohup");
      server.arguments = props;
      server.addDefaultServlets(null); // optional file servlet
      server.addServlet("/", dispatcher); // optional
      new Thread()
      {
         public void run()
         {
            server.serve();
         }
      }.start();
      ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
      dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
      dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());

   }

   @AfterClass
   public static void after() throws Exception
   {
      server.notifyStop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      POJOResourceFactory noDefaults = new POJOResourceFactory(SimpleResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      SimpleClient client = ProxyFactory.create(SimpleClient.class, "http://localhost:8081");

      Assert.assertEquals("basic", client.getBasic());
      client.putBasic("hello world");
      Assert.assertEquals("hello world", client.getQueryParam("hello world"));
      Assert.assertEquals(1234, client.getUriParam(1234));
   }


}