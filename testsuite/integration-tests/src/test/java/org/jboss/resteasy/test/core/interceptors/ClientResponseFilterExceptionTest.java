package org.jboss.resteasy.test.core.interceptors;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.ResponseProcessingException;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1932
 * @tpSince RESTEasy 3.0.26
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseFilterExceptionTest {

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ClientResponseFilterExceptionTest.class.getSimpleName());
      war.addClass(ClientResponseFilterExceptionResource.class);
      war.addClass(ClientResponseFilterExceptionFilter.class);
      return TestUtil.finishContainerPrepare(war, null, ClientResponseFilterExceptionResourceImpl.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClientResponseFilterExceptionTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Generate PreProcessorExceptionMapperCandlepinUnauthorizedException
    * @tpPassCrit SC_PRECONDITION_FAILED (412) HTTP code is excepted
    * @tpSince RESTEasy 3.0.26
    */
   @Test
   public void testMapper() throws Exception {
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

      ResteasyClient client = clientBuilder.build();

      ResteasyWebTarget target = client.target(generateURL(""));
      ClientResponseFilterExceptionResource service = target.proxy(ClientResponseFilterExceptionResource.class);

      int i = 0;
      for (; i < 10; i++) {
         try {
            service.dummy();
         } catch (InternalServerErrorException e) {
            //do nothing
         } catch (ResponseProcessingException e) {
            //do nothing
         }
      }
      Assert.assertEquals(10, i);
   }
}
