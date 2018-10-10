package org.jboss.resteasy.test.asynch;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.asynch.resource.AsyncUnhandledExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Unhandled exceptions should return 500 status
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncUnhandledExceptionTest {

   @Deployment
   public static Archive<?> createTestArchive() {

      WebArchive war = TestUtil.prepareArchive(AsyncUnhandledExceptionTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, AsyncUnhandledExceptionResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsyncUnhandledExceptionTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Unhandled exception is thrown from a ReadListener
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testPost() {
      ResteasyClient client = new ResteasyClientBuilderImpl().build();
      Response response = client.target(generateURL("/listener")).request().post(Entity.entity("aaa", "text/plain"));
      Assert.assertEquals(500, response.getStatus());
   }
   
   /**
   * @tpTestDetails Unhandled exception is thrown from a separate thread
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testGet() {
      ResteasyClient client = new ResteasyClientBuilderImpl().build();
      Response response = client.target(generateURL("/thread")).request().get();
      Assert.assertEquals(500, response.getStatus());
   }
}