package org.jboss.resteasy.test.grpc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jaxrs.example.CC1ServiceGrpc;
import jaxrs.example.CC1ServiceGrpc.CC1ServiceBlockingStub;
import jaxrs.example.CC1_proto;
import jaxrs.example.CC1_proto.FormMap;
import jaxrs.example.CC1_proto.FormValues;
import jaxrs.example.CC1_proto.GeneralEntityMessage;
import jaxrs.example.CC1_proto.GeneralReturnMessage;
import jaxrs.example.CC1_proto.ServletInfo;
import jaxrs.example.CC1_proto.gCookie;
import jaxrs.example.CC1_proto.gHeader;
import jaxrs.example.CC1_proto.gNewCookie;
import jaxrs.example.CC1_proto.gString;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC2;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC3;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC4;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC5;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC7;

/**
 * @tpSubChapter Jaxrs implementation
 * @tpChapter Integration tests
 * @tpTestCaseDetails RESTEASY-1531
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GrpcToJaxrsTest
{
   protected static final Logger log = Logger.getLogger(GrpcToJaxrsTest.class.getName());

   @Deployment
   public static Archive<?> deploy() {
         WebArchive war = TestUtil.prepareArchive(GrpcToJaxrsTest.class.getSimpleName());
         war.addClass(io.grpc.netty.shaded.io.netty.channel.group.ChannelMatchers.class);
         war.addClass(com.google.common.util.concurrent.internal.InternalFutureFailureAccess.class);
//         war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
////               + "Dependencies: com.google.guava services,org.jboss.resteasy.resteasy-grpc-provider services\n"));
//         + "Dependencies: com.google.guava services\n"));
         war.merge(ShrinkWrap.createFromZipFile( WebArchive.class, TestUtil.resolveDependency("jaxrs.example:jaxrs.example.grpc:war:0.0.25")));
//         TestUtil.addOtherLibrary(war, "jaxrs.example:jaxrs.example.grpc:jar:0.0.19");
         TestUtil.addOtherLibrary(war, "org.jboss.resteasy:grpc-bridge-runtime:jar:6.2.0.Final-SNAPSHOT");
         TestUtil.addOtherLibrary(war, "com.google.protobuf:protobuf-java:jar:3.17.3");
//         TestUtil.addOtherLibrary(war, "io.grpc:grpc-api:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-context:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-core:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-netty-shaded:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-protobuf:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-protobuf-lite:1.39.0");
         TestUtil.addOtherLibrary(war, "io.grpc:grpc-stub:1.39.0");
         TestUtil.addOtherLibrary(war, "io.perfmark:perfmark-api:0.23.0");
         war.addClass(jaxrs.example.CC1MessageBodyReaderWriter.class);
//         TestUtil.addOtherLibrary(war, "com.google.guava:failureaccess:jar:1.0.1");
//         TestUtil.addOtherLibrary(war, "com.google.guava:guava:jar:31.0.1-jre");
         war.setManifest(new StringAsset("Manifest-Version: 1.0\n"

               + "Dependencies: io.grpc, com.google.guava services, org.jboss.resteasy.grpc-bridge-runtime export, org.jboss.as.weld export services \n"));
         WebArchive archive = (WebArchive) TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
         log.info(archive.toString(true));
         archive.as(ZipExporter.class).exportTo(new File("/tmp/GrpcToJaxrs.jar"), true);
         return archive;
   }

   /*
//      io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder nsb;
      com.google.common.base.Preconditions p;
      WebArchive war = TestUtil.prepareArchive(GrpcToJaxrsTest.class.getSimpleName());
      war.addClass(io.grpc.netty.shaded.io.netty.channel.group.ChannelMatchers.class);
      war.addClass(com.google.common.util.concurrent.internal.InternalFutureFailureAccess.class);
      TestUtil.addOtherLibrary(war, "org.jboss.resteasy:grpc-bridge-runtime:jar:6.2.0.Final-SNAPSHOT");
      TestUtil.addOtherLibrary(war, "com.google.protobuf:protobuf-java:jar:3.17.3");
//      TestUtil.addOtherLibrary(war, "io.grpc:grpc-api:jar:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-netty-shaded:jar:1.39.0");
      war.merge(ShrinkWrap.createFromZipFile( WebArchive.class, TestUtil.resolveDependency("jaxrs.example:jaxrs.example.grpc:war:0.0.14")));
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
              + "Dependencies: org.jboss.resteasy.grpc-bridge-runtime export, io.grpc export, jakarta.enterprise.api export\n"));
      WebArchive archive = (WebArchive) TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
      log.info(archive.toString(true));
      archive.as(ZipExporter.class).exportTo(new File("/tmp/GrpcToJaxrs.jar"), true);
      return archive;
    */
//   private static String target = "localhost:9555";
   private static String target = "localhost:8082";
   private static CC1ServiceBlockingStub blockingStub;
   private static ManagedChannel channel;

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, GrpcToJaxrsTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/grpcToJaxrs/grpcserver/start")).request().get();
      log.info("status: " + response.getStatus());
      log.info("response: " + response.readEntity(String.class));
      response = client.target(generateURL("/grpcToJaxrs/p/ready")).request().get();
      log.info("status 2: " + response.getStatus());
      log.info("response 2: " + response.readEntity(String.class));
      channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      blockingStub = CC1ServiceGrpc.newBlockingStub(channel);
      int i = 0;
      for (i = 0; i < 5; i++) {
         try {
            ready();
            break;
         } catch (Exception e) {
            // keep trying
            Thread.sleep(1000);
         }
      }
      if (i == 5) {
         throw new RuntimeException("can't connect to gRPC server");
      } else {
         log.info("beforeClass() successful");
      }
   }

   static void ready() {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/ready").build();
      GeneralReturnMessage grm = blockingStub.ready(gem);
   }

   @Before
   public void before() {
      channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      blockingStub = CC1ServiceGrpc.newBlockingStub(channel);
   }

   @AfterClass
   public static void afterClass() throws InterruptedException {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
   }

   @Test
   public void testBoolean() throws Exception {
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGBooleanField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getBoolean(gem);
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response.getGBooleanField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testBooleanWithUnnecessaryURL() throws Exception {
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/boolean").setGBooleanField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getBoolean(gem);
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response.getGBooleanField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testBooleanWrapper() throws Exception {
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGBooleanField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getBooleanWrapper(gem);
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response.getGBooleanField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByte() throws Exception {
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGByteField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getByte(gem);
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response.getGByteField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByteWrapper() throws Exception {
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGByteField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getByteWrapper(gem);
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response.getGByteField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShort() throws Exception {
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGShortField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getShort(gem);
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response.getGShortField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShortWrapper() throws Exception {
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGShortField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getShortWrapper(gem);
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response.getGShortField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInt() throws Exception {
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGIntegerField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getInt(gem);
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response.getGIntegerField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInteger() throws Exception {
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setGIntegerField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getInteger(gem);
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response.getGIntegerField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testLong() throws Exception {
      jaxrs.example.CC1_proto.gLong n = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/long").setGLongField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getLong(gem);
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response.getGLongField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testLongWrapper() throws Exception {
      jaxrs.example.CC1_proto.gLong n = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Long").setGLongField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getLongWrapper(gem);
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response.getGLongField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testFloat() throws Exception {
      jaxrs.example.CC1_proto.gFloat n = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/float").setGFloatField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getFloat(gem);
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response.getGFloatField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testFloatWrapper() throws Exception {
      jaxrs.example.CC1_proto.gFloat n = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Float").setGFloatField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getFloat(gem);
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response.getGFloatField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testDouble() throws Exception {
      jaxrs.example.CC1_proto.gDouble n = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/double").setGDoubleField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getDouble(gem);
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response.getGDoubleField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testDoubleWrapper() throws Exception {
      jaxrs.example.CC1_proto.gDouble n = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Double").setGDoubleField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getDouble(gem);
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response.getGDoubleField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testChar() throws Exception {
      jaxrs.example.CC1_proto.gCharacter n = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("a").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/char").setGCharacterField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getChar(gem);
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response.getGCharacterField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCharacter() throws Exception {
      jaxrs.example.CC1_proto.gCharacter n = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("a").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Character").setGCharacterField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getChar(gem);
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response.getGCharacterField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testString() throws Exception {
      jaxrs.example.CC1_proto.gString n = jaxrs.example.CC1_proto.gString.newBuilder().setValue("abc").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/string").setGStringField(n).build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.getString(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("ABC").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testProduces() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/produces").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.produces(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("produces").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testConsumes() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/consumes").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.produces(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("consumes").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testPathParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/path/aa/param/bb").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.pathParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaaybbz").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testQueryParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/query?q1=a&q2=b").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.queryParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaybz").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testMatrixParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/matrix;m1=a;m2=b/more;m3=c").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.matrixParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("waxbycz").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   /**
    * Clarify treatment of cookies
    */
   @Test
   public void testCookieParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/cookieParams");
      gCookie.Builder cookieBuilder1 = gCookie.newBuilder();
      gCookie.Builder cookieBuilder2 = gCookie.newBuilder();
      gCookie cookie1 = cookieBuilder1.setName("c1").setValue("v1").setPath("a/b").setDomain("d1").build();
      gCookie cookie2 = cookieBuilder2.setName("c2").setValue("v2").build();
      messageBuilder.addCookies(cookie1).addCookies(cookie2);
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.cookieParams(gem);
         Assert.assertEquals("xc1=v1;d1,a/b,0yc2=v2;,,0z", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testHeaderParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080" + "/p/headerParams");
      gHeader.Builder headerBuilder1 = gHeader.newBuilder();
      gHeader header1 = headerBuilder1.addValues("v1.1").addValues("v1.2").build();
      messageBuilder.putHeaders("h1", header1);
      gHeader.Builder headerBuilder2 = gHeader.newBuilder();
      gHeader header2 = headerBuilder2.addValues("v2").build();
      messageBuilder.putHeaders("h2", header2);
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.headerParams(gem);
         Assert.assertEquals("xv1.1yv2z", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsList() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/list/pv2?q1=qv1&q1=qv2").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.paramsList(gem);
         gString expected = gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsSet() throws Exception {
      GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/set/pv2?q1=qv1&q1=qv2").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.paramsSet(gem);
         gString expected = gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsSortedSet() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/sortedset/pv2?q1=qv1&q1=qv2").build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.paramsSortedSet(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response.getGStringField());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testResponse() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      try {
         GeneralReturnMessage response = blockingStub.getResponse(gem);
         org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("cc7").build();
         org_jboss_resteasy_example___CC7 cc7 = org_jboss_resteasy_example___CC7.newBuilder().setM(11).setCC3Super(cc3).build();
         Any any = response.getGoogleProtobufAnyField();
         Assert.assertEquals(cc7, any.unpack(org_jboss_resteasy_example___CC7.class));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testSuspend() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/suspend");
      GeneralEntityMessage gem = messageBuilder.build();
      try {
         GeneralReturnMessage response = blockingStub.suspend(gem);
         Any any = response.getGoogleProtobufAnyField();
         gString gS = any.unpack(gString.class);
         String s = gS.getValue();
         Assert.assertEquals("suspend", s);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCompletionStage() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/async/cs");
      GeneralEntityMessage gem = messageBuilder.build();
      try {
         GeneralReturnMessage response = blockingStub.getResponseCompletionStage(gem);
         Assert.assertEquals("cs", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletContextPath() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.contextPath(gem);
         Assert.assertEquals("/GrpcToJaxrsTest", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletContextInitParam() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/servletContext");
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.servletContext(gem);
         Assert.assertEquals("/grpcToJaxrs", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletConfigServletName() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/servletConfig");
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.servletConfig(gem);
         Assert.assertEquals("CC1Servlet", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   //      @Test
   //      public void testSSE() throws Exception {
   //         jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
   //         messageBuilder.setURL("http://localhost:8080/p/sse");
   //         GeneralEntityMessage gem = messageBuilder.build();
   //         java.util.Iterator<jaxrs.example.CC1_proto.org_jboss_resteasy_plugins_protobuf_sse___SseEvent> events;
   //         try {
   //            events = blockingStub.sse(gem);
   //         } catch (StatusRuntimeException e) {
   //            Assert.fail("fail");
   //            return;
   //         }
   //         while (events.hasNext()) {
   //
   //         }
   //      }

   @Test
   public void testInheritance() throws Exception {
      org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("thag").build();
      org_jboss_resteasy_example___CC2 cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(17).setCC3Super(cc3).build();
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/inheritance").setOrgJbossResteasyExampleCC2Field(cc2);
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.inheritance(gem);
         cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("xthagy").build();
         cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(18).setCC3Super(cc3).build();
         Assert.assertTrue(cc2.equals(response.getOrgJbossResteasyExampleCC2Field()));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testReferenceField() throws Exception {
      org_jboss_resteasy_example___CC5 cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(11).build();
      org_jboss_resteasy_example___CC4 cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("grog").setCc5(cc5).build();
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/reference").setOrgJbossResteasyExampleCC4Field(cc4);
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.referenceField(gem);
         cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(12).build();
         cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("xgrogy").setCc5(cc5).build();
         Assert.assertTrue(cc4.equals(response.getOrgJbossResteasyExampleCC4Field()));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletInfo() throws Exception {
      ServletInfo servletInfo = ServletInfo.newBuilder()
            .setCharacterEncoding("utf-16")
            .setClientAddress("1.2.3.4")
            .setClientHost("bluemonkey")
            .setClientPort(7777).build();
      gString gstring = gString.newBuilder().setValue("servletInfo").build();
      GeneralEntityMessage gem  = GeneralEntityMessage.newBuilder()
            .setURL("http://localhost:8080/p/servletInfo")
            .setServletInfo(servletInfo)
            .setGStringField(gstring).build();
      try {
         GeneralReturnMessage response = blockingStub.testServletInfo(gem);
         Assert.assertTrue("UTF-16|1.2.3.5|BLUEMONKEY|7778".equals(response.getGStringField().getValue()));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   /**
    * Clarify treatment of cookies
    */
   @Test
   public void testServerCookies() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.serverCookies(gem);
         List<gNewCookie> list = response.getCookiesList();
         Assert.assertEquals(2, list.size());
         gNewCookie c1 = gNewCookie.newBuilder().setDomain("d1").setMaxAge(-1).setName("n1").setPath("p1").setValue("v1").build();
         gNewCookie c2 = gNewCookie.newBuilder().setDomain("d2").setMaxAge(17).setName("n2").setPath("p2").setValue("v2").setHttpOnly(true).setSecure(true).build();
         if ("n1".equals(list.get(0).getName())) {
            Assert.assertEquals(c1, list.get(0));
            Assert.assertEquals(c2, list.get(1));
         } else {
            Assert.assertEquals(c1, list.get(1));
            Assert.assertEquals(c2, list.get(0));
         }
         Assert.assertEquals("cookies", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServerHeaders() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.serverHeaders(gem);
         Map<String, CC1_proto.gHeader> headers = response.getHeadersMap();
         gHeader gh1 = gHeader.newBuilder().addValues("v1a").addValues("v1b").build();
         Assert.assertEquals(gh1, headers.get("h1"));
         gHeader gh2 = gHeader.newBuilder().addValues("v2").build();
         Assert.assertEquals(gh2, headers.get("h2"));
         Assert.assertEquals("headers", response.getGStringField().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletPath() throws Exception {
      String contextPath;
      {
         GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
         GeneralEntityMessage gem = messageBuilder.build();
         GeneralReturnMessage response;
         try {
            response = blockingStub.servletPath(gem);
            String result = response.getGStringField().getValue();

            // get context path
            int i = result.indexOf('|');
            contextPath = result.substring(0, i);

            // servlet path
            int j = result.indexOf('|', i + 1);
            Assert.assertEquals("", result.substring(i + 1, j));

            // path
            i = j + 1;
            j = result.indexOf('|', i);
            Assert.assertEquals(result.substring(i, j), "/p/servletPath");

            // HttpServletRequest.getPathTranslated()
            Assert.assertTrue(result.substring(j + 1).contains(File.separator + "p" + File.separator + "servletPath"));
         } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
         }
      }
      {
         GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
         GeneralEntityMessage gem = messageBuilder.setURL("http://localhost:8080" + contextPath + "/grpcToJaxrs/p/servletPath").build();
         GeneralReturnMessage response;
         try {
            response = blockingStub.servletPath(gem);
            String result = response.getGStringField().getValue();

            // context path
            int i = result.indexOf('|');
            Assert.assertEquals(contextPath, result.substring(0, i));

            // servlet path
            int j = result.indexOf('|', i + 1);
            Assert.assertEquals("/grpcToJaxrs", result.substring(i + 1, j));

            // path
            i = j + 1;
            j = result.indexOf('|', i);
            Assert.assertEquals(result.substring(i, j), "/p/servletPath");

            // HttpServletRequest.getPathTranslated()
            Assert.assertTrue(result.substring(j + 1).contains(File.separator + "p" + File.separator + "servletPath"));
         } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
         }
      }
   }

   @Test
   public void testServletParams() throws Exception {
      Map<String, FormValues> formMap = new HashMap<String, FormValues>();
      FormValues.Builder formValuesBuilderP2 = FormValues.newBuilder();
      formValuesBuilderP2.addFormValuesField("f2a").addFormValuesField("f2b");
      formMap.put("p2", formValuesBuilderP2.build());

      FormValues.Builder formValuesBuilderP3 = FormValues.newBuilder();
      formValuesBuilderP3.addFormValuesField("f3a").addFormValuesField("f3b");
      formMap.put("p3", formValuesBuilderP3.build());

      FormMap.Builder formMapBuilder = FormMap.newBuilder();
      formMapBuilder.putAllFormMapField(formMap);
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setFormField(formMapBuilder.build());

      messageBuilder.setURL("http://localhost:8080/p/servletParams?p1=q1&p2=q2");
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.servletParams(gem);
         String s = response.getGStringField().getValue();
         Assert.assertTrue(s.startsWith("q1|q2|f2a|f3a"));
         Assert.assertTrue(s.contains("p1->q1"));
         Assert.assertTrue(s.contains("p2->f2af2bq2"));
         Assert.assertTrue(s.contains("p3->f3af3b"));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   /**
    * Clarify treatment of cookies
    */
   @Test
   public void testJaxrsResponse() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.jaxrsResponse(gem);
         Assert.assertEquals(2, response.getCookiesCount());
         gNewCookie expectedCookie1 = gNewCookie.newBuilder().setDomain("d1").setName("n1").setPath("p1").setValue("v1")
               .setMaxAge(11).setExpiry(Timestamp.newBuilder().setSeconds(111)).setHttpOnly(true).setVersion(1).build();
         gNewCookie expectedCookie2 = gNewCookie.newBuilder().setDomain("d2").setName("n2").setPath("p2").setValue("v2")
               .setMaxAge(17).setExpiry(Timestamp.newBuilder().setSeconds(222)).setSecure(true).setVersion(1).build();
         Assert.assertTrue(expectedCookie1.equals(response.getCookies(0)) && expectedCookie2.equals(response.getCookies(1))
                        || expectedCookie1.equals(response.getCookies(1)) && expectedCookie2.equals(response.getCookies(0)));
         Map<String, CC1_proto.gHeader> headers = response.getHeadersMap();
         Assert.assertEquals(1, headers.get("h1").getValuesCount());
         Assert.assertEquals("v1",  headers.get("h1").getValues(0));
         Assert.assertEquals(222, response.getStatus().getValue());
         Assert.assertEquals(1, headers.get("Content-Type").getValuesCount());
         Assert.assertEquals("x/y", headers.get("Content-Type").getValues(0));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletResponse() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.servletResponse(gem);
         Map<String, CC1_proto.gHeader> headers = response.getHeadersMap();

         Assert.assertEquals(1, headers.get("d1").getValuesCount());
         Assert.assertEquals(1, headers.get("h1").getValuesCount());
         Assert.assertEquals(1, headers.get("i1").getValuesCount());

         Assert.assertEquals(2, headers.get("d2").getValuesCount());
         Assert.assertEquals(2, headers.get("h2").getValuesCount());
         Assert.assertEquals(2, headers.get("i2").getValuesCount());

         Assert.assertEquals(1, headers.get("d3").getValuesCount());
         Assert.assertEquals(1, headers.get("h3").getValuesCount());
         Assert.assertEquals(1, headers.get("i3").getValuesCount());

         Assert.assertTrue(headers.get("d1").getValues(0).contains("02 Jan 1970"));
         Assert.assertEquals("v1",  headers.get("h1").getValues(0));
         Assert.assertEquals("13",  headers.get("i1").getValues(0));

         Assert.assertTrue(headers.get("d2").getValues(0).contains("03 Jan 1970"));
         Assert.assertTrue(headers.get("d2").getValues(1).contains("04 Jan 1970"));
         Assert.assertEquals("v2a", headers.get("h2").getValues(0));
         Assert.assertEquals("v2b", headers.get("h2").getValues(1));
         Assert.assertEquals("19",  headers.get("i2").getValues(0));
         Assert.assertEquals("29",  headers.get("i2").getValues(1));

         Assert.assertTrue(headers.get("d3").getValues(0).contains("06 Jan 1970"));
         Assert.assertEquals("v3b", headers.get("h3").getValues(0));
         Assert.assertEquals("41",  headers.get("i3").getValues(0));

         Assert.assertEquals(1, response.getCookiesCount());
         gNewCookie expectedCookie = gNewCookie.newBuilder().setDomain("d1").setMaxAge(3).setName("n1").setPath("p1").setValue("v1").build();
         Assert.assertEquals(expectedCookie, response.getCookies(0));

         Assert.assertEquals(223, response.getStatus().getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail 2");
         return;
      }
   }

   @Test
   public void testInnerClass() {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.inner(gem);
         CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass.Builder builder = CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass.newBuilder();
         CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass inner = builder.setI(3).setS("three").build();
         Assert.assertEquals(inner, response.getOrgJbossResteasyExampleCC1INNERInnerClassField());
      } catch (StatusRuntimeException e) {
          Assert.fail("fail");
          return;
      }
   }

   @Test
   public void testLocatorGet() {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("/p/locator/get").setHttpMethod("GET");
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.locator(gem);
         Assert.assertEquals("/p/locator/get", response.getGoogleProtobufAnyField().unpack(CC1_proto.gString.class).getValue());
      } catch (Exception e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testLocatorPost() {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("/p/locator/post/abc").setHttpMethod("POST");
      messageBuilder.setGoogleProtobufAnyField(Any.pack(gString.newBuilder().setValue("xyz").build()));
      GeneralEntityMessage gem = messageBuilder.build();
      GeneralReturnMessage response;
      try {
         response = blockingStub.locator(gem);
         Assert.assertEquals("abc|xyz", response.getGoogleProtobufAnyField().unpack(CC1_proto.gString.class).getValue());
      } catch (Exception e) {
         Assert.fail("fail");
         return;
      }
   }
}
