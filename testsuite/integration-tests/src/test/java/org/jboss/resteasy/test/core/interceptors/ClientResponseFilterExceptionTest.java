package org.jboss.resteasy.test.core.interceptors;

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

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1932
 * @tpSince RESTEasy 4.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseFilterExceptionTest {
   
   private static ResteasyClient client;
   private static ClientResponseFilterExceptionResource service;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ClientResponseFilterExceptionTest.class.getSimpleName());
      war.addClass(ClientResponseFilterExceptionResource.class);
      war.addClass(ClientResponseFilterExceptionFilter.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, ClientResponseFilterExceptionResourceImpl.class);
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
      ResteasyWebTarget target = client.target(generateURL(""));
      service = target.proxy(ClientResponseFilterExceptionResource.class);
   }
   
   @After
   public void after() {
      client.close();
   }

   /**
    * @tpTestDetails 
    * @tpPassCrit
    * @tpSince RESTEasy 4.0
    */
   @Test
   public void testSync() throws Exception {
      doTest(() -> service.sync());
   }
   
   @Test
   public void testCompletionStage() throws Exception{
      doTest(() -> service.cs());
   }
   
   @Test
   public void testSingle() throws Exception {
      doTest(() -> service.single());
   }
   
   @Test
   public void testObservable() throws Exception {
      doTest(() -> service.observable());
   }
   
   @Test
   public void testFlowable() throws Exception {
      doTest(() -> service.flowable());
   }
   
   void doTest(Supplier<?> supplier) throws Exception {
      int i = 0;
      for (i = 0; i < 10; i++) {
         try {
            supplier.get();
         } catch (Exception e) {
            //do nothing
         } 
      }
      Assert.assertEquals(10, i);
   }
}
