package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class SseEventSinkTest
{

   private final static Logger logger = Logger.getLogger(SseEventSinkTest.class);

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(SseTest.class.getSimpleName());
      war.addClass(SseEventSinkTest.class);
      war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/web.xml", "web.xml");
      war.addAsWebResource("org/jboss/resteasy/test/providers/sse/index.html", "index.html");
      war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new RuntimePermission("modifyThread")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, SseApplication.class, GreenHouse.class, SseResource.class,
            AnotherSseResource.class, EscapingSseResource.class, ExecutorServletContextListener.class);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, SseTest.class.getSimpleName());
   }

   @After
   public void stopSendEvent() throws Exception
   {
      Client isOpenClient = ClientBuilder.newClient();
      Invocation.Builder isOpenRequest = isOpenClient.target(generateURL("/service/server-sent-events/stopevent"))
            .request();
      isOpenRequest.get();

   }

   @Test
   public void testCloseByEvnetSource() throws Exception
   {
      final CountDownLatch latch = new CountDownLatch(5);
      final List<String> results = new ArrayList<String>();
      final AtomicInteger errors = new AtomicInteger(0);
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/service/server-sent-events/events"));
      SseEventSource eventSource = SseEventSource.target(target).build();

      Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
      eventSource.register(event -> {
         results.add(event.readData(String.class));
         latch.countDown();
      }, ex -> {
         errors.incrementAndGet();
         logger.error(ex.getMessage(), ex);
         throw new RuntimeException(ex);
      });
      eventSource.open();

      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertEquals(0, errors.get());
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Client isOpenClient = ClientBuilder.newClient();
      Invocation.Builder isOpenRequest = isOpenClient.target(generateURL("/service/server-sent-events/isopen"))
            .request();

      javax.ws.rs.core.Response response = isOpenRequest.get();
      Assert.assertTrue("EventSink open is expected ", response.readEntity(Boolean.class));

      eventSource.close();
      Assert.assertFalse("Closed eventSource state is expceted", eventSource.isOpen());

      WebTarget messageTarget = ClientBuilder.newClient().target(generateURL("/service/server-sent-events"));
      for (int counter = 0; counter < 5; counter++)
      {
         String msg = "messageAfterClose";
         messageTarget.request().post(Entity.text(msg));
      }

      Assert.assertTrue("EventSource should not receive msg after it is closed",
            results.indexOf("messageAfterClose") == -1);
      Assert.assertFalse("EventSink close is expected ", isOpenRequest.get().readEntity(Boolean.class));

   }
}
