package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.microprofile.MicroprofileClientBuilderResolver;
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
public class RestClientProxyTest
{

   @ArquillianResource
   URL url;

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(RestClientProxyTest.class.getSimpleName());
      war.addClass(RestClientProxyTest.class);
      war.addPackage(HelloResource.class.getPackage());
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addClass(Category.class);
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
      RestClientBuilder resteasyBuilder = MicroprofileClientBuilderResolver.instance().newBuilder();
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

}