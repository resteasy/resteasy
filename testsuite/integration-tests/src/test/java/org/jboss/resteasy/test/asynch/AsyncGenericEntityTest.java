package org.jboss.resteasy.test.asynch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.asynch.resource.AsyncGenericEntityMessageBodyWriter;
import org.jboss.resteasy.test.asynch.resource.AsyncGenericEntityResource;
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
 * @tpTestCaseDetails Test getting GenericType from return entity.
 * @tpSince RESTEasy 3.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncGenericEntityTest {

   @Deployment()
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(AsyncGenericEntityTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null,
            AsyncGenericEntityMessageBodyWriter.class,
            AsyncGenericEntityResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsyncGenericEntityTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test getting GenericType from return entity.
    * @tpSince RESTEasy 3.7.0
    */
   @Test
   public void testCalls() {
       Client client = ClientBuilder.newClient();
       Builder request = client.target(generateURL("/test")).request();
       Response response = request.get();
       Assert.assertEquals(200, response.getStatus());
       Assert.assertEquals("ok", response.readEntity(String.class));
       response.close();
       client.close();
   }

}
