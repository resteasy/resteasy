package org.jboss.resteasy.test.response;

import java.util.PropertyPermission;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.response.resource.AsyncResponseCallback;
import org.jboss.resteasy.test.response.resource.AsyncResponseException;
import org.jboss.resteasy.test.response.resource.AsyncResponseExceptionMapper;
import org.jboss.resteasy.test.response.resource.PublisherResponseRawStreamResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Publisher response type
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class PublisherResponseRawStreamTest {

   Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(PublisherResponseRawStreamTest.class.getSimpleName());
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
            + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.reactivestreams\n"));
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new PropertyPermission("*", "read"),
            new PropertyPermission("*", "write"),
            new RuntimePermission("modifyThread")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, PublisherResponseRawStreamResource.class,
            AsyncResponseCallback.class, AsyncResponseExceptionMapper.class, AsyncResponseException.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, PublisherResponseRawStreamTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      client = ClientBuilder.newClient();
   }

   @After
   public void close() {
      client.close();
      client = null;
   }

   /**
    * @tpTestDetails Resource method returns Publisher<String>.
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void testChunked() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/chunked")).request();
      Response response = request.get();
      String entity = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(entity.startsWith("0-11-12-1"));
      Assert.assertTrue(entity.endsWith("29-1"));
   }

   /**
    * @tpTestDetails Resource method unsubscribes on close for infinite streams.
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void testInfiniteStreamsChunked() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/chunked-infinite")).request();
      Future<Response> futureResponse = request.async().get();
      try
      {
         futureResponse.get(2, TimeUnit.SECONDS);
      }
      catch(TimeoutException x)
      {
      }
      close();
      setup();
      Thread.sleep(5000);
      request = client.target(generateURL("/infinite-done")).request();
      Response response = request.get();
      String entity = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("true", entity);
   }
}
