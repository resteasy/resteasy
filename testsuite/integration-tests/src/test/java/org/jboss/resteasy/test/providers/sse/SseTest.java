package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class SseTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseTest.class.getSimpleName());
        war.addClass(SseTest.class);
        war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/web.xml","web.xml");
        war.addAsWebResource("org/jboss/resteasy/test/providers/sse/index.html","index.html");
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, SseApplication.class, GreenHouse.class, SseResource.class, AnotherSseResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseTest.class.getSimpleName());
    }
    
    @Test
    public void testAddMessage() throws Exception
    {
       final CountDownLatch latch = new CountDownLatch(5);
       final List<String> results = new ArrayList<String>();
       Client client = ClientBuilder.newBuilder().build();
       WebTarget target = client.target(generateURL("/service/server-sent-events"));

       SseEventSource eventSource = SseEventSource.target(target).build();
       Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
       eventSource.register(event -> {
            results.add(event.toString());
            latch.countDown();
          });
       eventSource.open();

       Client messageClient = new ResteasyClientBuilder().connectionPoolSize(10).build();
       WebTarget messageTarget = messageClient.target(generateURL("/service/server-sent-events"));
       for (int counter = 0; counter < 5; counter++)
       {
          messageTarget.request().post(Entity.text("message " + counter));
       } 
       Assert.assertTrue("Waiting for event to be delivered has timed out.", latch.await(30, TimeUnit.SECONDS));
       messageTarget.request().delete();
       messageClient.close();
       eventSource.close();
       client.close();
       
       Assert.assertTrue("5 messages are expected, but is : " + results.size(), results.size() == 5);
    }
    
    @Test
    public void testSseEvent() throws Exception
    {
       final List<String> results = new ArrayList<String>();
       final CountDownLatch latch = new CountDownLatch(6);
       Client client = new ResteasyClientBuilder().connectionPoolSize(10).build();
       WebTarget target = client.target(generateURL("/service/server-sent-events")).path("domains").path("1");

       SseEventSource eventSource = SseEventSource.target(target).build();
       Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
       eventSource.register(event -> {
          results.add(event.readData());
          latch.countDown();
       });
       eventSource.open();

       Assert.assertTrue("Waiting for event to be delivered has timed out.", latch.await(10, TimeUnit.SECONDS));
       Assert.assertTrue("6 SseInboundEvent expected", results.size() == 6);
       Assert.assertTrue("Expect the last event is Done event, but it is :" + results.toArray(new String[]
             {})[5], results.toArray(new String[]
       {})[5].indexOf("Done") > -1);
       target.request().delete();
       eventSource.close();
       client.close();
    }
    
    
    @Test
    public void testBroadcast() throws Exception
    {
       final CountDownLatch latch = new CountDownLatch(2);
       Client client = new ResteasyClientBuilder().connectionPoolSize(10).build();
       WebTarget target = client.target(generateURL("/service/server-sent-events/subscribe"));
       final String textMessage = "This is broadcast message";

       SseEventSource eventSource = SseEventSource.target(target).build();
       Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
       eventSource.register(event -> {
          latch.countDown();
       });
       eventSource.open();
       eventSource.register(insse -> {Assert.assertTrue("", textMessage.equals(insse.readData()));});
            
       Client client2 = new ResteasyClientBuilder().build();
       WebTarget target2 = client2.target(generateURL("/service/sse/subscribe"));

       SseEventSource eventSource2 = SseEventSource.target(target2).build();
       eventSource2.register(event -> {
          latch.countDown();
       });
       eventSource2.open();
       
       //Test for eventSource subscriber
       eventSource2.register(insse -> {Assert.assertTrue("", textMessage.equals(insse.readData()));});
       //To give some time to subscribe, otherwise the broadcast will execute before subscribe
       Thread.sleep(3000);
       client.target(generateURL("/service/server-sent-events/broadcast")).request().post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS)); 
       Assert.assertTrue("Waiting for broadcast event to be delivered has timed out.", latch.await(10, TimeUnit.SECONDS));
       eventSource.close();
       eventSource2.close();
       client.close();
       client2.close();
    }

//    @Test
//    //This will open a browser and test with html sse client
//    public void testHtmlSse() throws Exception
//    {
//       
//       Runtime runtime = Runtime.getRuntime();
//       try
//       {
//          runtime.exec("xdg-open " + generateURL(""));
//       }
//       catch (IOException e)
//       {
//
//       }
//       Thread.sleep(30 * 1000);
//    }
}
