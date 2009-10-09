package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.jboss.resteasy.star.messaging.DestinationResource;
import org.jboss.resteasy.star.messaging.TopicResource;
import org.jboss.resteasy.star.messaging.MessageRepository;
import org.jboss.resteasy.star.messaging.CurrentMessageIndex;
import org.jboss.resteasy.star.messaging.SimpleDeployment;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.server.HornetQ;
import org.hornetq.core.server.HornetQServer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicTest extends BaseResourceTest
{
   public static SimpleDeployment server;

   @BeforeClass
   public static void setup() throws Exception
   {
      server = new SimpleDeployment();
      server.getTopics().add("test");
      server.setRegistry(deployment.getRegistry());
      server.start();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      server.stop();
   }

   

   @Test
   public void testBasic() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/topics/test"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getLinkHeader().getLinkByTitle("sender");
      Link top = response.getLinkHeader().getLinkByTitle("top-message");

      Assert.assertEquals(504, top.request().get().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(1)).post().getStatus());
      Link next = top;
      ClientResponse<String> res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(1), res.getEntity());
      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next-message");
      Assert.assertEquals(504, next.request().get().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(2)).post().getStatus());
      Assert.assertEquals(201, sender.request().body("text/plain", Integer.toString(3)).post().getStatus());


      res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(2), res.getEntity());

      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next-message");
      res = next.request().get(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals(Integer.toString(3), res.getEntity());


      System.out.println("***: " + res.getLinkHeader());
      next = res.getLinkHeader().getLinkByTitle("next-message");
      Assert.assertEquals(504, next.request().get().getStatus());


   }
}
