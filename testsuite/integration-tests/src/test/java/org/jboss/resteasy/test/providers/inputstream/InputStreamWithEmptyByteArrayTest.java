package org.jboss.resteasy.test.providers.inputstream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.providers.inputstream.resource.InputStreamWithEmptyByteArrayInterface;
import org.jboss.resteasy.test.providers.inputstream.resource.InputStreamWithEmptyByteArrayResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Sending empty byte array with InputStreamProvider causes problems in HttpClient
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1407
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputStreamWithEmptyByteArrayTest {

   static ResteasyClient client;
   static InputStreamWithEmptyByteArrayInterface proxy;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(InputStreamWithEmptyByteArrayTest.class.getSimpleName());
      war.addClass(InputStreamWithEmptyByteArrayInterface.class);
      return TestUtil.finishContainerPrepare(war, null, InputStreamWithEmptyByteArrayResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, InputStreamWithEmptyByteArrayTest.class.getSimpleName());
   }

   @Before
   public void init() {
      client = new ResteasyClientBuilderImpl().build();
      proxy = client.target(generateURL("")).proxy(InputStreamWithEmptyByteArrayInterface.class);
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Send empty byte array
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testEmptyByteArray() throws IOException {
      Response response = proxy.upload(new ByteArrayInputStream(new byte[0]));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Send nonempty byte array
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testNonEmptyByteArray() throws IOException {
      Response response = proxy.upload(new ByteArrayInputStream("test".getBytes()));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("test", response.readEntity(String.class));
   }
}
