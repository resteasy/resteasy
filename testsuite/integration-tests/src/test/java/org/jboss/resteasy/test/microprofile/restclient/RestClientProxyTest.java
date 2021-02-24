package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.microprofile.client.BuilderResolver;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import io.reactivex.Single;

@RunWith(Arquillian.class)
@RunAsClient
public class RestClientProxyTest
{

   public static final String EMOJIS = "\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00\uD83D\uDE00";
   @ArquillianResource
   URL url;

   @Deployment(testable = false)
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(RestClientProxyTest.class.getSimpleName());
      war.addClass(RestClientProxyTest.class);
      war.addClass(HelloResource.class);
      war.addClass(HeaderPropagator.class);
      war.addPackage(HelloResource.class.getPackage());
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addClass(Category.class);
      war.addClasses(TestParamConverter.class, TestParamConverterProvider.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient,org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, RestClientProxyTest.class.getSimpleName());
   }

   @Test
   public void testGetClient() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      assertEquals("Hello", client.hello());
   }

   @Test
   public void testGetSingle() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<String> value = new AtomicReference<String>();
      value.set(null);
      Single<String> single = client.single("foo");
      single.subscribe((String s) -> {
         value.set(s);
         latch.countDown();
      });
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      assertEquals("foo", value.get());
   }

   //RESTEASY-2633
   @Test
   public void testEncodingMultiByteCharacters() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<String> value = new AtomicReference<String>();
      value.set(null);
      Single<String> single = client.single(EMOJIS);
      single.subscribe((String s) -> {
         value.set(s);
         latch.countDown();
      });
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      assertEquals(EMOJIS, value.get());
   }

   @Test
   public void testGetCompletionStage() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<String> value = new AtomicReference<String>();
      value.set(null);
      CompletionStage<String> cs = client.cs("foo");
      cs.whenComplete((String s, Throwable t) -> {
         value.set(s);
         latch.countDown();
      });
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      assertEquals("foo", value.get());
   }

   @Test
   public void testHeadersPropagation() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<String> value = new AtomicReference<String>();
      value.set(null);
      CompletionStage<String> cs = client.asyncClient();
      cs.whenComplete((String s, Throwable t) -> {
         value.set(s);
         latch.countDown();
      });
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      assertEquals("OK", value.get());

      assertEquals("OK", client.client());
   }

   @Test
   public void testAsyncClient404() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference<Throwable> value = new AtomicReference<Throwable>();
      value.set(null);
      CompletionStage<String> cs = client.asyncClient404();
      cs.whenComplete((String s, Throwable t) -> {
         value.set(t.getCause());
         latch.countDown();
      });
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      assertTrue(value.get() instanceof WebApplicationException);
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ((WebApplicationException)value.get()).getResponse().getStatus());
   }

   @Test
   public void testNullPathParam() throws Exception {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder
              .baseUrl(new URL(generateURL("")))
              .build(HelloClient.class);

      assertNotNull(client);
      assertEquals("testPath", client.nullPathParam("testPath"));
      try {
         client.nullPathParam(null);
         fail("A null path parameter should not be allowed");
      } catch (NullPointerException e) {
         // Given the generic error we want to ensure we're getting an NPE from the correct spot. We'll validate this
         // via the message id.
         final String msg = e.getLocalizedMessage();
         final String msgId = "RESTEASY004690";
         assertTrue(String.format("Expected message to start with %s: %s", msgId, msg), msg.startsWith(msgId));
      }
   }

   @Test
   public void testNullPathParamWithConverter() throws Exception {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder
              .baseUrl(new URL(generateURL("")))
              .register(TestParamConverterProvider.class)
              .build(HelloClient.class);

      assertNotNull(client);
      assertEquals("testPath", client.nullPathParam("testPath"));
      try {
         client.nullPathParam(null);
         fail("A null path parameter should not be allowed");
      } catch (NullPointerException e) {
         // Given the generic error we want to ensure we're getting an NPE from the correct spot. We'll validate this
         // via the message id.
         final String msg = e.getLocalizedMessage();
         final String msgId = "RESTEASY004690";
         assertTrue(String.format("Expected message to start with %s: %s", msgId, msg), msg.startsWith(msgId));
      }
   }

   @Test
   public void testNullQueryParam() throws Exception {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder
              .baseUrl(new URL(generateURL("")))
              .build(HelloClient.class);

      assertNotNull(client);
      assertEquals("testPath", client.nullQueryParam("testPath"));
      assertNull(client.nullQueryParam(null));
   }

   @Test
   public void testNullQueryParamWithConverter() throws Exception {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder
              .baseUrl(new URL(generateURL("")))
              .register(TestParamConverterProvider.class)
              .build(HelloClient.class);

      assertNotNull(client);
      assertEquals("testPath", client.nullQueryParam("testPath"));
      assertNull(client.nullQueryParam(null));
   }

   public static class TestParamConverter implements ParamConverter<String> {

      @Override
      public String fromString(final String value) {
         return value;
      }

      @Override
      public String toString(final String value) {
         return value.toString();
      }
   }

   @Provider
   public static class TestParamConverterProvider implements ParamConverterProvider {
      private final TestParamConverter converter = new TestParamConverter();

      @Override
      @SuppressWarnings("unchecked")
      public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
         if (Objects.equals(rawType, CharSequence.class)) {
            return (ParamConverter<T>) converter;
         }
         return null;
      }
   }
}