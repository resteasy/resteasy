package org.jboss.resteasy.test.providers.sse;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.LocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.providers.jsonb.JsonBindingProvider;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeUser;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@RunWith(Arquillian.class)
@RunAsClient
public class SseJsonEventTest
{
   private final static Logger logger = Logger.getLogger(SseJsonEventTest.class);

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(SseJsonEventTest.class.getSimpleName());
      Map<String, String> contextParam = new HashMap<>();
      contextParam.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
      return TestUtil.finishContainerPrepare(war, contextParam, SseSmokeUser.class, SseSmokeResource.class);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, SseJsonEventTest.class.getSimpleName());
   }

   @Test
   public void testWithoutProvider() throws Exception
   {
      final CountDownLatch latch = new CountDownLatch(1);
      final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
      final AtomicInteger errors = new AtomicInteger(0);
      ClientBuilder clientBuilder = ClientBuilder.newBuilder();
      Configuration config = clientBuilder.property("resteasy.preferJacksonOverJsonB", true).getConfiguration();
      ResteasyProviderFactory.pushContext(Configuration.class, config);
      Client client = clientBuilder.withConfig(config).build();
      try
      {
         WebTarget target = client.target(generateURL("/sse/eventsjson"));
         SseEventSource msgEventSource = SseEventSource.target(target).build();
         try (SseEventSource eventSource = msgEventSource)
         {
            eventSource.register(event -> {
               results.add(event);
               latch.countDown();
            }, ex -> {
               errors.incrementAndGet();
               logger.error(ex.getMessage(), ex);
               throw new RuntimeException(ex);
            });
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         }

      }
      finally
      {
         ResteasyProviderFactory.getContextDataMap().remove(Configuration.class);
         client.close();
      }
      Assert.assertEquals("One message was expected.", 1, results.size());
      try
      {
         results.get(0).readData(SseSmokeUser.class, MediaType.APPLICATION_JSON_TYPE);
         fail("Exception is expected");
      }
      catch (ProcessingException e)
      {
         Assert.assertTrue("exception is not expected", e.getMessage().indexOf("Failed to read data") > -1);
      }
   }

   @Test
   public void testEventWithCustomProvider() throws Exception
   {
      final CountDownLatch latch = new CountDownLatch(1);
      final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
      final AtomicInteger errors = new AtomicInteger(0);
      ClientBuilder clientBuilder = ClientBuilder.newBuilder();
      Configuration config = clientBuilder.property("resteasy.preferJacksonOverJsonB", true).getConfiguration();
      ResteasyProviderFactory.pushContext(Configuration.class, config);
      Client client = clientBuilder.withConfig(config).build();
      try
      {
         client.property("resteasy.preferJacksonOverJsonB", true);
         ResteasyProviderFactory.pushContext(Configuration.class, client.getConfiguration());
         WebTarget target = client.target(generateURL("/sse/eventsjson"));
         target.register(CustomJacksonProvider.class);
         SseEventSource msgEventSource = SseEventSource.target(target).build();
         try (SseEventSource eventSource = msgEventSource)
         {
            eventSource.register(event -> {
               results.add(event);
               latch.countDown();
            }, ex -> {
               errors.incrementAndGet();
               logger.error(ex.getMessage(), ex);
               throw new RuntimeException(ex);
            });
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         }
         Assert.assertEquals("One message was expected.", 1, results.size());

         Assert.assertEquals("user name is not expected", "Zeytin",
               results.get(0).readData(SseSmokeUser.class, MediaType.APPLICATION_JSON_TYPE).getUsername());

      }
      finally
      {
         ResteasyProviderFactory.getContextDataMap().remove(Configuration.class);
         client.close();
      }
   }
}
