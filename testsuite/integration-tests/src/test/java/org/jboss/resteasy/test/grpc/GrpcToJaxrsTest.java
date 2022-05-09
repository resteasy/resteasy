package org.jboss.resteasy.test.grpc;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jaxrs.example.CC1ServiceGrpc;
//import jaxrs.example.CC1ServiceGrpc.CC1ServiceBlockingStub;
import jaxrs.example.CC1_proto.GeneralEntityMessage;
import jaxrs.example.CC1_proto.ServletInfo;
import jaxrs.example.CC1_proto.gString;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC2;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC3;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC4;
import jaxrs.example.CC1_proto.org_jboss_resteasy_example___CC5;

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
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(GrpcToJaxrsTest.class.getSimpleName());
      //      File jaxrsExampleFile = TestUtil.resolveDependency("jaxrs.example:jaxrs.example.grpc:jar:0.0.1-SNAPSHOT");
      //      JarFile jaxrsExampleWar = new JarFile(jaxrsExampleFile.getPath());
      //      File classJar = TestUtil.extractClasses(jaxrsExampleWar);
//      TestUtil.addOtherLibrary(war, "jaxrs.example:jaxrs.example.grpc:jar:0.0.1-SNAPSHOT");
//      TestUtil.addOtherLibrary(war, "jaxrs.example:jaxrs.example.grpc:war:0.0.1-SNAPSHOT");
//      war.addClasses(CC1.class, CC2.class, CC3.class, CC4.class, CC5.class, CC6.class, CC7.class);
//      war.addClass(CC1_Server.class);
//      war.addClass(CC1_proto.class);
//      war.addClass(CC1_JavabufTranslator.class);
//      war.addClass(CC1MessageBodyReaderWriter.class);
//      war.addClass(CC1ServiceGrpc.class);
//      war.addClass(CC1ServiceGrpcImpl.class);
//      war.addClass(CC1_Server.class);
//      war.addClasses(CC2.class, CC3.class, CC4.class, CC5.class, CC6.class, CC7.class);
      war.addClass(io.grpc.netty.shaded.io.netty.channel.group.ChannelMatchers.class);
      war.addClass(com.google.common.util.concurrent.internal.InternalFutureFailureAccess.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
//            + "Dependencies: com.google.guava services,org.jboss.resteasy.resteasy-grpc-provider services\n"));
      + "Dependencies: com.google.guava services\n"));
      TestUtil.addOtherLibrary(war, "org.jboss.resteasy:grpc-bridge:jar:6.1.0-SNAPSHOT");
      TestUtil.addOtherLibrary(war, "com.github.javaparser:javaparser-symbol-solver-core:jar:3.24.2");
      TestUtil.addOtherLibrary(war, "com.github.javaparser:javaparser-core:jar:3.24.2");
      TestUtil.addOtherLibrary(war, "org.wildfly:wildfly-weld-jakarta:jar:26.1.0.Final");
      TestUtil.addOtherLibrary(war, "org.wildfly:wildfly-weld-common-jakarta:jar:26.1.0.Final");
      TestUtil.addOtherLibrary(war, "org.wildfly.core:wildfly-server:jar:18.1.0.Final");
      TestUtil.addOtherLibrary(war, "com.google.protobuf:protobuf-java:jar:3.17.3");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-api:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-context:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-core:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-netty-shaded:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-protobuf:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-protobuf-lite:1.39.0");
      TestUtil.addOtherLibrary(war, "io.grpc:grpc-stub:1.39.0");
      TestUtil.addOtherLibrary(war, "io.perfmark:perfmark-api:0.23.0");
      TestUtil.addOtherLibrary(war, "org.jboss.weld.se:weld-se-core:jar:5.0.0.CR2");
      TestUtil.addOtherLibrary(war, "org.jboss.weld.environment:weld-environment-common:jar:5.0.0.CR2");
      TestUtil.addOtherLibrary(war, "jaxrs.example:jaxrs.example.grpc:jar:0.0.2-SNAPSHOT");
      TestUtil.addOtherLibrary(war, "com.google.guava:failureaccess:jar:1.0.1");
      WebArchive archive = (WebArchive) TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
      log.info(archive.toString(true));
      archive.as(ZipExporter.class).exportTo(new File("/tmp/GrpcToJaxrs.jar"), true);
      return archive;
   }

//   private static String target = "localhost:9555";
 private static String target = "localhost:8082";
//   private static CC1ServiceGrpc csg;
   private static CC1ServiceGrpc.CC1ServiceBlockingStub blockingStub;
   private static ManagedChannel channel;
   org.jboss.weld.environment.se.WeldContainer x;
   org.jboss.weld.environment.ContainerInstance y;

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, GrpcToJaxrsTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
            // Create a communication channel to the server, known as a Channel. Channels are thread-safe
            // and reusable. It is common to create channels at the beginning of your application and reuse
            // them until the application shuts down.
      channel = ManagedChannelBuilder.forTarget(target)
//             Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
//             needing certificates.
            .usePlaintext()
            .build();
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/p/context")).request().get();
      log.info("status: " + response.getStatus());
      log.info("response: " + response.readEntity(String.class));
      response = client.target(generateURL("/root/grpcserver/start")).request().get();
      log.info("status: " + response.getStatus());
      log.info("response: " + response.readEntity(String.class));

      response = client.target(generateURL("/root/grpcserver/context")).request().get();
      Assert.assertEquals(200, response.getStatus());
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
      }
   }

   static void ready() {
      log.info("in ready()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/ready").build();
      blockingStub.ready(gem);
      log.info("ready");
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
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/boolean").setGBooleanField(n).build();
      jaxrs.example.CC1_proto.gBoolean response;
      try {
         response = blockingStub.getBoolean(gem);
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testBooleanWrapper() throws Exception {
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Boolean").setGBooleanField(n).build();
      jaxrs.example.CC1_proto.gBoolean response;
      try {
         response = blockingStub.getBooleanWrapper(gem);
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByte() throws Exception {
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/byte").setGByteField(n).build();
      jaxrs.example.CC1_proto.gByte response;
      try {
         response = blockingStub.getByte(gem);
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByteWrapper() throws Exception {
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Byte").setGByteField(n).build();
      jaxrs.example.CC1_proto.gByte response;
      try {
         response = blockingStub.getByteWrapper(gem);
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShort() throws Exception {
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/short").setGShortField(n).build();
      jaxrs.example.CC1_proto.gShort response;
      try {
         response = blockingStub.getShort(gem);
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShortWrapper() throws Exception {
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Short").setGShortField(n).build();
      jaxrs.example.CC1_proto.gShort response;
      try {
         response = blockingStub.getShortWrapper(gem);
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInt() throws Exception {
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/int").setGIntegerField(n).build();
      jaxrs.example.CC1_proto.gInteger response;
      try {
         response = blockingStub.getInt(gem);
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInteger() throws Exception {
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Integer").setGIntegerField(n).build();
      jaxrs.example.CC1_proto.gInteger response;
      try {
         response = blockingStub.getInteger(gem);
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gLong response;
      try {
         response = blockingStub.getLong(gem);
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gLong response;
      try {
         response = blockingStub.getLongWrapper(gem);
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gFloat response;
      try {
         response = blockingStub.getFloat(gem);
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gFloat response;
      try {
         response = blockingStub.getFloat(gem);
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gDouble response;
      try {
         response = blockingStub.getDouble(gem);
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gDouble response;
      try {
         response = blockingStub.getDouble(gem);
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gCharacter response;
      try {
         response = blockingStub.getChar(gem);
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gCharacter response;
      try {
         response = blockingStub.getChar(gem);
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response);
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
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.getString(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("ABC").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testProduces() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/produces").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.produces(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("produces").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testConsumes() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/consumes").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.produces(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("consumes").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testPathParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/path/aa/param/bb").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.pathParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaaybbz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testQueryParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/query?q1=a&q2=b").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.queryParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaybz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testMatrixParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/matrix;m1=a;m2=b/more;m3=c").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.matrixParams(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("waxbycz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCookieParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/cookieParams");
      jaxrs.example.CC1_proto.Cookie.Builder cookieBuilder1 = jaxrs.example.CC1_proto.Cookie.newBuilder();
      jaxrs.example.CC1_proto.Cookie.Builder cookieBuilder2 = jaxrs.example.CC1_proto.Cookie.newBuilder();
      jaxrs.example.CC1_proto.Cookie cookie1 = cookieBuilder1.setName("c1").setValue("v1").setVersion(7).setPath("a/b").setDomain("d1").build();
      jaxrs.example.CC1_proto.Cookie cookie2 = cookieBuilder2.setName("c2").setValue("v2").build();
      messageBuilder.addCookies(cookie1).addCookies(cookie2);
      GeneralEntityMessage gem = messageBuilder.build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.cookieParams(gem);
         Assert.assertEquals("xc1=v1;d1,a/b,7yc2=v2;,,0z", response.getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testHeaderParams() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080" + "/p/headerParams");
      jaxrs.example.CC1_proto.Header.Builder headerBuilder1 = jaxrs.example.CC1_proto.Header.newBuilder();
      jaxrs.example.CC1_proto.Header header1 = headerBuilder1.addValues("v1.1").addValues("v1.2").build();
      messageBuilder.putHeaders("h1", header1);
      jaxrs.example.CC1_proto.Header.Builder headerBuilder2 = jaxrs.example.CC1_proto.Header.newBuilder();
      jaxrs.example.CC1_proto.Header header2 = headerBuilder2.addValues("v2").build();
      messageBuilder.putHeaders("h2", header2);
      GeneralEntityMessage gem = messageBuilder.build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.headerParams(gem);
         Assert.assertEquals("xv1.1yv2z", response.getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsList() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", jaxrs.example.CC1_proto.Header.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/list/pv2?q1=qv1&q1=qv2").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.paramsList(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsSet() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", jaxrs.example.CC1_proto.Header.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/set/pv2?q1=qv1&q1=qv2").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.paramsSet(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testParamsSortedSet() throws Exception {
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      builder.putHeaders("h1", jaxrs.example.CC1_proto.Header.newBuilder().addValues("hv1").addValues("hv2").build());
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/sortedset/pv2?q1=qv1&q1=qv2").build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.paramsSortedSet(gem);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
         Assert.assertEquals(expected, response);
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
         com.google.protobuf.Any response = blockingStub.suspend(gem);
         jaxrs.example.CC1_proto.gString gS = response.unpack(jaxrs.example.CC1_proto.gString.class);
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
         jaxrs.example.CC1_proto.gString response = blockingStub.getResponseCompletionStage(gem);
         Assert.assertEquals("cs", response.getValue());
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletContext() throws Exception {
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/context");
      GeneralEntityMessage gem = messageBuilder.build();
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.context(gem);
         Assert.assertEquals("/" + GrpcToJaxrsTest.class.getSimpleName(), response.getValue());
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
      org_jboss_resteasy_example___CC2 response;
      try {
         response = blockingStub.inheritance(gem);
         cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("xthagy").build();
         cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(18).setCC3Super(cc3).build();
         Assert.assertTrue(cc2.equals(response));
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
      org_jboss_resteasy_example___CC4 response;
      try {
         response = blockingStub.referenceField(gem);
         cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(12).build();
         cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("xgrogy").setCc5(cc5).build();
         Assert.assertTrue(cc4.equals(response));
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
         gString response = blockingStub.testServletInfo(gem);
         Assert.assertTrue("UTF-16|1.2.3.5|BLUEMONKEY|7778".equals(response.getValue()));
      } catch (StatusRuntimeException e) {
         Assert.fail("fail");
         return;
      }
   }
}
