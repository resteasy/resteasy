package org.jboss.resteasy.test.stream;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.stream.resource.StreamRawByteArrayMessageBodyReaderWriter;
import org.jboss.resteasy.test.stream.resource.StreamRawByteMessageBodyReaderWriter;
import org.jboss.resteasy.test.stream.resource.StreamRawCharArrayMessageBodyReaderWriter;
import org.jboss.resteasy.test.stream.resource.StreamRawCharMessageBodyReaderWriter;
import org.jboss.resteasy.test.stream.resource.StreamRawMediaTypes;
import org.jboss.resteasy.test.stream.resource.StreamRawObservableRxJava2Resource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * These tests check raw streaming.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class StreamRawObservableRxJava2Test {

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(StreamRawObservableRxJava2Test.class.getSimpleName());
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services"));
      return TestUtil.finishContainerPrepare(war, null,
         StreamRawObservableRxJava2Resource.class,
         StreamRawByteMessageBodyReaderWriter.class,
         StreamRawByteArrayMessageBodyReaderWriter.class,
         StreamRawCharMessageBodyReaderWriter.class,
         StreamRawCharArrayMessageBodyReaderWriter.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, StreamRawObservableRxJava2Test.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testByte() throws Exception {
      doTestByte("default");
      doTestByte("false");
      doTestByte("true");
   }

   void doTestByte(String include) {
      Invocation.Builder request = client.register(StreamRawByteMessageBodyReaderWriter.class).target(generateURL("/byte/" + include)).request();
      Response response = request.get();
      StreamRawMediaTypes.testMediaType("byte", include, MediaType.valueOf(response.getHeaderString("Content-Type")));
      byte[] entity = response.readEntity(byte[].class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(3, entity.length);
      for (int i = 0; i < 3; i++) {
         Assert.assertEquals((byte) i, entity[i]);
      }
   }

   @Test
   public void testByteArray() throws Exception {
      doTestByteArray("default");
      doTestByteArray("false");
      doTestByteArray("true");
   }

   void doTestByteArray(String include) {
      Invocation.Builder request = client.target(generateURL("/bytes/" + include)).request();
      Response response = request.get();
      StreamRawMediaTypes.testMediaType("byte", include, MediaType.valueOf(response.getHeaderString("Content-Type")));
      byte[] entity = response.readEntity(byte[].class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(9, entity.length);
      byte[] expected = new byte[] {0, 1, 2, 0, 1, 2, 0, 1, 2};
      for (int i = 0; i < 9; i++) {
         Assert.assertEquals(expected[i], entity[i]);
      }
   }

   @Test
   public void testChar() throws Exception {
      doTestChar("default");
      doTestChar("false");
      doTestChar("true");
   }

   void doTestChar(String include) {
      Invocation.Builder request = client.target(generateURL("/char/" + include)).request();
      Response response = request.get();
      StreamRawMediaTypes.testMediaType("char", include, MediaType.valueOf(response.getHeaderString("Content-Type")));
      String entity = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("abc", entity);
   }

   @Test
   public void testCharArray() throws Exception {
      doTestCharArray("default");
      doTestCharArray("false");
      doTestCharArray("true");
   }

   void doTestCharArray(String include) {
      Invocation.Builder request = client.register(StreamRawCharArrayMessageBodyReaderWriter.class).target(generateURL("/chars/" + include)).request();
      Response response = request.get();
      StreamRawMediaTypes.testMediaType("char", include, MediaType.valueOf(response.getHeaderString("Content-Type")));
      Character[] entity = response.readEntity(Character[].class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(9, entity.length);
      Character[] chars = new Character[] {'a', 'b', 'c', 'a', 'b', 'c','a', 'b', 'c'};
      for (int i = 0; i < entity.length; i++)
      {
         Assert.assertEquals(chars[i], entity[i]);
      }
   }
}
