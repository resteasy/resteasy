package org.jboss.resteasy.test.client;

import java.io.File;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.test.client.resource.FakeHttpServer;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Verify request is sent in chunked format
 * @tpSince RESTEasy 3.1.4
 */
public class ChunkedTransferEncodingUnitTest
{
   private static final String testFilePath;

   static {
      testFilePath = TestUtil.getResourcePath(ChunkedTransferEncodingUnitTest.class, "ChunkedTransferEncodingUnitTestFile");
   }

   @Rule
   public FakeHttpServer fakeHttpServer = new FakeHttpServer();


   @Test
   public void testChunkedTarget() throws Exception {
      fakeHttpServer.start();

      ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
      ResteasyWebTarget target = client.target("http://" + fakeHttpServer.getHostAndPort() + "/chunked");
      target.setChunked(true);
      ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
      File file = new File(testFilePath);
      Response response = request.post(Entity.entity(file, "text/plain"));
      String header = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", header);
      response.close();
      client.close();
   }

   @Test
   public void testChunkedRequest() throws Exception {
      fakeHttpServer.start();

      ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
      ResteasyWebTarget target = client.target("http://" + fakeHttpServer.getHostAndPort() + "/chunked");
      ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
      request.setChunked(true);
      File file = new File(testFilePath);
      Response response = request.post(Entity.entity(file, "text/plain"));
      String header = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", header);
      response.close();
      client.close();
   }
}
