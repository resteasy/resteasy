package org.jboss.resteasy.test.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.test.client.resource.InputStreamResourceClient;
import org.jboss.resteasy.test.client.resource.InputStreamResourceService;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Read and write InputStreams
 * @tpSince RESTEasy 3.0.20
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputStreamResourceTest extends ClientTestBase{

   static Client resteasyClient;

   @BeforeClass
   public static void setup() {
       resteasyClient = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
       resteasyClient.close();
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(InputStreamResourceTest.class.getSimpleName());
       return TestUtil.finishContainerPrepare(war, null, InputStreamResourceService.class);
   }

   /**
    * @tpTestDetails Read Strings as either Strings or InputStreams
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testClientResponse() throws Exception {
       InputStreamResourceClient client = ProxyBuilder.builder(InputStreamResourceClient.class, resteasyClient.target(generateURL(""))).build();
       Assert.assertEquals("hello", client.getAsString());
       Response is = client.getAsInputStream();
       Assert.assertEquals("hello", new String(ReadFromStream.readFromStream(1024, is.readEntity(InputStream.class))));
       is.close();
       client.postString("new value");
       Assert.assertEquals("new value", client.getAsString());
       client.postInputStream(new ByteArrayInputStream("new value 2".getBytes()));
       Assert.assertEquals("new value 2", client.getAsString());
   }
}