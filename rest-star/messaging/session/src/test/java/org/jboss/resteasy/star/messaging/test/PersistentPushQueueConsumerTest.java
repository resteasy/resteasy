package org.jboss.resteasy.star.messaging.test;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.star.messaging.MessageServiceConfiguration;
import org.jboss.resteasy.star.messaging.MessageServiceManager;
import org.jboss.resteasy.star.messaging.queue.QueueDeployment;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;
import org.jboss.resteasy.star.messaging.queue.push.xml.XmlLink;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * Test durable queue push consumers
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PersistentPushQueueConsumerTest
{
   public static MessageServiceManager manager;
   public static File pushStore;
   protected static ResteasyDeployment deployment;
   public static HornetQServer hornetqServer;

   @BeforeClass
   public static void setup() throws Exception
   {
      pushStore = File.createTempFile("push-store", ".xml");
      FileOutputStream fos = new FileOutputStream(pushStore);
      fos.write("<push-store/>".getBytes());
      fos.close();
   }

   @AfterClass
   public static void cleanup() throws Exception
   {
      pushStore.delete();
   }

   public static void startup() throws Exception
   {
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      hornetqServer = HornetQServers.newHornetQServer(configuration);
      hornetqServer.start();

      deployment = EmbeddedContainer.start();
      manager = new MessageServiceManager();
      MessageServiceConfiguration config = new MessageServiceConfiguration();
      config.setQueuePushStoreFile(pushStore.toString());
      manager.setConfiguration(config);
      manager.start();
      deployment.getRegistry().addSingletonResource(manager.getQueueManager().getDestination());
      deployment.getRegistry().addSingletonResource(manager.getTopicManager().getDestination());


   }

   public static void shutdown() throws Exception
   {
      manager.stop();
      manager = null;
      EmbeddedContainer.stop();
      deployment = null;
      hornetqServer.stop();
      hornetqServer = null;
   }

   @Test
   public void testSuccessFirst() throws Exception
   {
      System.out.println("temp file: " + pushStore);
      startup();
      deployQueues();

      ClientRequest request = new ClientRequest(generateURL("/queues/testQueue"));

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + sender);
      Link pushSubscriptions = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "push-subscriptions");
      System.out.println("push subscriptions: " + pushSubscriptions);

      request = new ClientRequest(generateURL("/queues/forwardQueue"));
      response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link forwardSender = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "create");
      System.out.println("create: " + forwardSender);
      Link consumeNext = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), response, "consume-next");
      System.out.println("poller: " + consumeNext);

      PushRegistration reg = new PushRegistration();
      reg.setDurable(true);
      XmlLink target = new XmlLink(forwardSender);
      reg.setTarget(target);
      response = pushSubscriptions.request().body("application/xml", reg).post();
      Assert.assertEquals(201, response.getStatus());

      shutdown();
      startup();
      deployQueues();

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      Thread.sleep(100);
      res = consumeNext.request().post(String.class);

      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      Link session = BaseMessageTest.getLinkByTitle(manager.getQueueManager().getLinkStrategy(), res, "session");
      Assert.assertEquals(204, session.request().delete().getStatus());

      shutdown();
   }

   private void deployQueues()
           throws Exception
   {
      QueueDeployment deployment = new QueueDeployment();
      deployment.setDuplicatesAllowed(true);
      deployment.setDurableSend(false);
      deployment.setName("testQueue");
      manager.getQueueManager().deploy(deployment);
      QueueDeployment deployment2 = new QueueDeployment();
      deployment2.setDuplicatesAllowed(true);
      deployment2.setDurableSend(false);
      deployment2.setName("forwardQueue");
      manager.getQueueManager().deploy(deployment2);
   }
}