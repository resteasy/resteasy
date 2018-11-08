package org.jboss.resteasy.test.core.interceptors;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.factory.ApacheHttpClient4EngineFactory;
import org.jboss.resteasy.test.core.interceptors.resource.ClientResponseFilterExceptionFilter;
import org.jboss.resteasy.test.core.interceptors.resource.ClientResponseFilterExceptionResource;
import org.jboss.resteasy.test.core.interceptors.resource.ClientResponseFilterExceptionResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1932
 * @tpSince RESTEasy 3.6.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseFilterExceptionTest {

   private static ResteasyClient client;
   private static ClientResponseFilterExceptionResource service;
   private static CountDownLatch latch;;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ClientResponseFilterExceptionTest.class.getSimpleName());
      war.addClass(ClientResponseFilterExceptionResource.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, ClientResponseFilterExceptionFilter.class, ClientResponseFilterExceptionResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClientResponseFilterExceptionTest.class.getSimpleName());
   }

   @Before
   public void before() {
      RequestConfig requestConfig = RequestConfig.custom()
         .setConnectionRequestTimeout(1000)
         .setSocketTimeout(1000)
         .setConnectTimeout(1000)
         .build();

      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
         .setDefaultRequestConfig(requestConfig)
         .setMaxConnPerRoute(2)
         .setMaxConnTotal(2);

      ClientHttpEngine engine = ApacheHttpClient4EngineFactory.create(httpClientBuilder.build(), true);

      ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder().httpEngine(engine)
         .register(ClientResponseFilterExceptionFilter.class)
         ;

      client = clientBuilder.build();
      ResteasyWebTarget target = client.target(generateURL("/"));
      service = target.proxy(ClientResponseFilterExceptionResource.class);
      latch = new CountDownLatch(10);
   }

   @After
   public void after() {
      client.close();
   }

   /**
    * @tpTestDetails test synchronous call
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testSync() throws Exception {
      int i = 0;
      for (i = 0; i < 10; i++) {
         try {
            service.sync();
         } catch (Exception e) {
            incr(e);
         }
      }
      Assert.assertEquals(0, latch.getCount());
   }

   /**
    * @tpTestDetails test asynchronous call: CompletionStage
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testCompletionStage() throws Exception {
      Assert.assertTrue(
         doTest(
            () ->  service.cs(),
            (CompletionStage<String> cs) -> {try {cs.toCompletableFuture().get();} catch (Exception e) {incr(e);}}
            ));
   }

   /**
    * @tpTestDetails test asynchronous call: Single
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testSingle() throws Exception {
      Assert.assertTrue(
         doTest(
            () ->  service.single(),
            (Single<String> single) -> single.subscribe(o -> {}, t -> incr(t))
            ));
   }

   /**
    * @tpTestDetails test asynchronous call: Observable
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testObservable() throws Exception {
      Assert.assertTrue(
         doTest(
            () ->  service.observable(),
            (Observable<String> observable) -> observable.subscribe(o -> {}, t -> incr(t))
            ));
   }

   /**
    * @tpTestDetails test asynchronous call: Flowable
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testFlowable() throws Exception {
      Assert.assertTrue(
         doTest(
            () ->  service.flowable(),
            (Flowable<String> flowable) -> flowable.subscribe(o -> {}, t -> incr(t))
            ));
   }

   ///////////////////////////////////////////////////////////////////////////////////
   static void incr(Throwable t) {
      if (t.getMessage().contains("ClientResponseFilterExceptionFilter")) {
         latch.countDown();
      }
   }

   static <T> boolean doTest(Supplier<T> supplier, Consumer<T> consumer) throws InterruptedException {
      int i = 0;
      for (i = 0; i < 10; i++) {
         T o = supplier.get();
         consumer.accept(o);
      }
      latch.await(10, TimeUnit.SECONDS);
      return latch.getCount() == 0;
   }
}
