package org.hornetq.rest.test;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.rest.MessageServiceManager;
import org.hornetq.rest.queue.push.xml.XmlLink;
import org.hornetq.rest.topic.PushTopicRegistration;
import org.hornetq.rest.topic.TopicDeployment;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * Test durable queue push consumers
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PersistentPushTopicConsumerTest
{
   public static HornetQServer server;
   public static MessageServiceManager manager;
   protected static ResteasyDeployment deployment;

   @BeforeClass
   public static void setup() throws Exception
   {
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      server = HornetQServers.newHornetQServer(configuration);
      server.start();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      manager.getTopicManager().getPushStore().removeAll();
      server.stop();
      server = null;
   }

   public static void startup() throws Exception
   {
      deployment = EmbeddedContainer.start();


      manager = new MessageServiceManager();
      manager.start();
      deployment.getRegistry().addSingletonResource(manager.getQueueManager().getDestination());
      deployment.getRegistry().addSingletonResource(manager.getTopicManager().getDestination());

      deployment.getRegistry().addPerRequestResource(Receiver.class);

   }

   public static void shutdown() throws Exception
   {
      manager.stop();
      manager = null;
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      startup();
      deployTopic();

      ClientRequest request = new ClientRequest(generateURL("/topics/testTopic"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link pushSubscriptions = MessageTestBase.getLinkByTitle(manager.getTopicManager().getLinkStrategy(), response, "push-subscriptions");
      System.out.println("push subscriptions: " + pushSubscriptions);

      String sub1 = generateURL("/subscribers/1");
      String sub2 = generateURL("/subscribers/2");

      PushTopicRegistration reg = new PushTopicRegistration();
      reg.setDurable(true);
      XmlLink target = new XmlLink();
      target.setHref(sub1);
      reg.setTarget(target);
      response = pushSubscriptions.request().body("application/xml", reg).post();
      Assert.assertEquals(201, response.getStatus());

      reg = new PushTopicRegistration();
      reg.setDurable(true);
      target = new XmlLink();
      target.setHref(sub2);
      reg.setTarget(target);
      response = pushSubscriptions.request().body("application/xml", reg).post();
      Assert.assertEquals(201, response.getStatus());

      shutdown();
      startup();
      deployTopic();

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      Receiver.latch.await(1, TimeUnit.SECONDS);

      Assert.assertEquals("1", Receiver.subscriber1);
      Assert.assertEquals("1", Receiver.subscriber2);

      manager.getTopicManager().getPushStore().removeAll();

      shutdown();
   }

   @Path("/subscribers")
   public static class Receiver
   {
      public static String subscriber1;
      public static String subscriber2;
      public static CountDownLatch latch = new CountDownLatch(2);

      @Path("1")
      @POST
      @Consumes("text/plain")
      public void postOne(String msg)
      {
         System.out.println("in subscribers 1!!!!!!!!!! " + msg);
         subscriber1 = msg;
         latch.countDown();
      }

      @Path("2")
      @POST
      @Consumes("text/plain")
      public void postTwo(String msg)
      {
         System.out.println("in subscribers 2!!!!!!!!!! " + msg);
         subscriber2 = msg;
         latch.countDown();
      }
   }

   private void deployTopic()
           throws Exception
   {
      TopicDeployment deployment = new TopicDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testTopic");
      manager.getTopicManager().deploy(deployment);


   }
}