package org.jboss.resteasy.test.grpc;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
import jaxrs.example.CC1ServiceGrpc.CC1ServiceBlockingStub;
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
//      war.addClass(CC1_proto.class);
//      war.addClass(CC1_JavabufTranslator.class);
//      war.addClass(CC1MessageBodyReaderWriter.class);
//      war.addClass(CC1ServiceGrpc.class);
//      war.addClass(CC1ServiceGrpcImpl.class);
//      war.addClass(CC1_Server.class);
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
//      war.addClass(GrpcToJaxrsTest.class);
      WebArchive archive = (WebArchive) TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
      System.out.println(archive.toString(true));
      archive.as(ZipExporter.class).exportTo(
            new File("/home/rsigal/tmp/grpc.042622/git.resteasy.main.grpc/Resteasy/testsuite/integration-tests/GrpcToJaxrs.jar"), true);
      return archive;
   }

//   private static String target = "localhost:9555";
 private static String target = "localhost:8082";
//   private static CC1ServiceGrpc csg;
   private static CC1ServiceBlockingStub blockingStub;
   private static ManagedChannel channel;
   org.jboss.weld.environment.se.WeldContainer x;
   org.jboss.weld.environment.ContainerInstance y;
   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, GrpcToJaxrsTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      System.out.println("entered beforeClass()");
      System.out.println("calling " + generateURL("/grpcserver/start"));

            // Create a communication channel to the server, known as a Channel. Channels are thread-safe
            // and reusable. It is common to create channels at the beginning of your application and reuse
            // them until the application shuts down.
      channel = ManagedChannelBuilder.forTarget(target)
//             Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
//             needing certificates.
            .usePlaintext()
            .build();
      System.out.println("created channel");
//      blockingStub = CC1ServiceGrpc.newBlockingStub(channel);
//      System.out.println("created blockingStub: " + blockingStub);
//      Thread.sleep(1111111);
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/p/context")).request().get();
      System.out.println("status: " + response.getStatus());
      System.out.println("response: " + response.readEntity(String.class));
      response = client.target(generateURL("/grpcserver/start")).request().get();
      System.out.println("status: " + response.getStatus());
      System.out.println("response: " + response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      blockingStub = CC1ServiceGrpc.newBlockingStub(channel);
      //http://localhost:8080/jaxrs.example.grpc-0.0.1-SNAPSHOT/root/grpcserver/start
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
      System.out.println("finished beforeClass()");
   }

   static void ready() {
      System.out.println("in ready()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/ready").build();
      blockingStub.ready(gem);
      System.out.println("ready");
   }

   @Before
   public void before() {
      channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
      blockingStub = CC1ServiceGrpc.newBlockingStub(channel);
   }

   @AfterClass
   public static void afterClass() throws InterruptedException {
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      System.out.println("finished afterClass()");
   }

   @Test
   public void testBoolean() throws Exception {
      System.out.println("running testBoolean()");
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/boolean").setGBooleanField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gBoolean response;
      try {
         response = blockingStub.getBoolean(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testBooleanWrapper() throws Exception {
      System.out.println("running testBooleanWrapper()");
      jaxrs.example.CC1_proto.gBoolean n = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(false).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Boolean").setGBooleanField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gBoolean response;
      try {
         response = blockingStub.getBooleanWrapper(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gBoolean expected = jaxrs.example.CC1_proto.gBoolean.newBuilder().setValue(true).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByte() throws Exception {
      System.out.println("running testByte()");
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/byte").setGByteField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gByte response;
      try {
         response = blockingStub.getByte(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testByteWrapper() throws Exception {
      System.out.println("running testByteWrapper()");
      jaxrs.example.CC1_proto.gByte n = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Byte").setGByteField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gByte response;
      try {
         response = blockingStub.getByteWrapper(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gByte expected = jaxrs.example.CC1_proto.gByte.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShort() throws Exception {
      System.out.println("running testShort()");
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/short").setGShortField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gShort response;
      try {
         response = blockingStub.getShort(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testShortWrapper() throws Exception {
      System.out.println("running testShortWrapper()");
      jaxrs.example.CC1_proto.gShort n = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(7).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Short").setGShortField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gShort response;
      try {
         response = blockingStub.getShortWrapper(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gShort expected = jaxrs.example.CC1_proto.gShort.newBuilder().setValue(8).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInt() throws Exception {
      System.out.println("running testInt()");
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/int").setGIntegerField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gInteger response;
      try {
         response = blockingStub.getInt(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testInteger() throws Exception {
      System.out.println("running testInteger()");
      jaxrs.example.CC1_proto.gInteger n = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Integer").setGIntegerField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gInteger response;
      try {
         response = blockingStub.getInteger(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gInteger expected = jaxrs.example.CC1_proto.gInteger.newBuilder().setValue(4).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testLong() throws Exception {
      System.out.println("running testLong()");
      jaxrs.example.CC1_proto.gLong n = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/long").setGLongField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gLong response;
      try {
         response = blockingStub.getLong(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testLongWrapper() throws Exception {
      System.out.println("running testLongWrapper()");
      jaxrs.example.CC1_proto.gLong n = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Long").setGLongField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gLong response;
      try {
         response = blockingStub.getLongWrapper(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gLong expected = jaxrs.example.CC1_proto.gLong.newBuilder().setValue(4L).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testFloat() throws Exception {
      System.out.println("running testFloat()");
      jaxrs.example.CC1_proto.gFloat n = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/float").setGFloatField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gFloat response;
      try {
         response = blockingStub.getFloat(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testFloatWrapper() throws Exception {
      System.out.println("running testFloatWrapper()");
      jaxrs.example.CC1_proto.gFloat n = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Float").setGFloatField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gFloat response;
      try {
         response = blockingStub.getFloat(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gFloat expected = jaxrs.example.CC1_proto.gFloat.newBuilder().setValue(4.0f).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testDouble() throws Exception {
      System.out.println("running testDouble()");
      jaxrs.example.CC1_proto.gDouble n = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/double").setGDoubleField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gDouble response;
      try {
         response = blockingStub.getDouble(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testDoubleWrapper() throws Exception {
      System.out.println("running testDoubleWrapper()");
      jaxrs.example.CC1_proto.gDouble n = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Double").setGDoubleField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gDouble response;
      try {
         response = blockingStub.getDouble(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gDouble expected = jaxrs.example.CC1_proto.gDouble.newBuilder().setValue(4.0d).build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testChar() throws Exception {
      System.out.println("running testChar()");
      jaxrs.example.CC1_proto.gCharacter n = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("a").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/char").setGCharacterField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gCharacter response;
      try {
         response = blockingStub.getChar(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCharacter() throws Exception {
      System.out.println("running testCharacter()");
      jaxrs.example.CC1_proto.gCharacter n = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("a").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Character").setGCharacterField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gCharacter response;
      try {
         response = blockingStub.getChar(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gCharacter expected = jaxrs.example.CC1_proto.gCharacter.newBuilder().setValue("A").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testString() throws Exception {
      System.out.println("running testString()");
      jaxrs.example.CC1_proto.gString n = jaxrs.example.CC1_proto.gString.newBuilder().setValue("abc").build();
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/string").setGStringField(n).build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.getString(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("ABC").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testProduces() throws Exception {
      System.out.println("running testProduces()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/produces").build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.produces(gem);
         System.out.println("produces: " + response);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("produces").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testConsumes() throws Exception {
      System.out.println("running testConsumes()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/consumes").build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.produces(gem);
         System.out.println("consumes: " + response);
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("consumes").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testPathParams() throws Exception {
      System.out.println("running testPathParams()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/path/aa/param/bb").build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.pathParams(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaaybbz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testQueryParams() throws Exception {
      System.out.println("running testQueryParams()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/query?q1=a&q2=b").build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.queryParams(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("xaybz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testMatrixParams() throws Exception {
      System.out.println("running testMatrixParams()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder builder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/matrix;m1=a;m2=b/more;m3=c").build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.matrixParams(gem);
         System.out.println("response: " + response.getValue());
         jaxrs.example.CC1_proto.gString expected = jaxrs.example.CC1_proto.gString.newBuilder().setValue("waxbycz").build();
         Assert.assertEquals(expected, response);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCookieParams() throws Exception {
      System.out.println("running testCookieParams()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/cookieParams");
      jaxrs.example.CC1_proto.Cookie.Builder cookieBuilder1 = jaxrs.example.CC1_proto.Cookie.newBuilder();
      jaxrs.example.CC1_proto.Cookie.Builder cookieBuilder2 = jaxrs.example.CC1_proto.Cookie.newBuilder();
      jaxrs.example.CC1_proto.Cookie cookie1 = cookieBuilder1.setName("c1").setValue("v1").setVersion(7).setPath("a/b").setDomain("d1").build();
      jaxrs.example.CC1_proto.Cookie cookie2 = cookieBuilder2.setName("c2").setValue("v2").build();
      messageBuilder.addCookies(cookie1).addCookies(cookie2);
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.cookieParams(gem);
         System.out.println("response: " + response.getValue());
         Assert.assertEquals("xc1=v1;d1,a/b,7yc2=v2;,,0z", response.getValue());
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testHeaderParams() throws Exception {
      System.out.println("blockingStub: " + blockingStub);
      System.out.println("channel: " + channel);
//Thread.sleep(11111111);
      System.out.println("running testHeaderParams()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080" + "/p/headerParams");
      jaxrs.example.CC1_proto.Header.Builder headerBuilder1 = jaxrs.example.CC1_proto.Header.newBuilder();
      jaxrs.example.CC1_proto.Header header1 = headerBuilder1.addValues("v1.1").addValues("v1.2").build();
      messageBuilder.putHeaders("h1", header1);
      jaxrs.example.CC1_proto.Header.Builder headerBuilder2 = jaxrs.example.CC1_proto.Header.newBuilder();
      jaxrs.example.CC1_proto.Header header2 = headerBuilder2.addValues("v2").build();
      messageBuilder.putHeaders("h2", header2);
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         System.out.println("blockingStub: " + blockingStub);
         response = blockingStub.headerParams(gem);
         System.out.println("response: \"" + response.getValue() + "\"");
         Assert.assertEquals("xv1.1yv2z", response.getValue());
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testSuspend() throws Exception {
      System.out.println("running testSuspend()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/suspend");
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      try {
         com.google.protobuf.Any response = blockingStub.suspend(gem);
         jaxrs.example.CC1_proto.gString gS = response.unpack(jaxrs.example.CC1_proto.gString.class);
         String s = gS.getValue();
         System.out.println("response: " + response);
         System.out.println("unpacked response: " + gS);
         System.out.println("s: " + s);
         Assert.assertEquals("suspend", s);
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testCompletionStage() throws Exception {
      System.out.println("running testCompletionStage()");
      jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/async/cs");
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      try {
         jaxrs.example.CC1_proto.gString response = blockingStub.getResponseCompletionStage(gem);
         System.out.println("response: " + response.getValue());
         Assert.assertEquals("cs", response.getValue());
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletContext() throws Exception {
      System.out.println("running testServletContext()");
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/context");
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      jaxrs.example.CC1_proto.gString response;
      try {
         response = blockingStub.context(gem);
         System.out.println("response: " + response.getValue());
         Assert.assertEquals("/" + GrpcToJaxrsTest.class.getSimpleName(), response.getValue());
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   //      @Test
   //      public void testSSE() throws Exception {
   //         System.out.println("running testSSE()");
   //         jaxrs.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jaxrs.example.CC1_proto.GeneralEntityMessage.newBuilder();
   //         messageBuilder.setURL("http://localhost:8080/p/sse");
   //         GeneralEntityMessage gem = messageBuilder.build();
   //         System.out.println("gem: " + gem);
   //         
   //         java.util.Iterator<jaxrs.example.CC1_proto.org_jboss_resteasy_plugins_protobuf_sse___SseEvent> events;
   //         try {
   //            events = blockingStub.sse(gem);
   //            System.out.println("events: " + events);
   //         } catch (StatusRuntimeException e) {
   //            e.printStackTrace();
   //            Assert.fail("fail");
   //            return;
   //         }
   //         while (events.hasNext()) {
   //            
   //         }
   //      }

   @Test
   public void testInheritance() throws Exception {
      System.out.println("running testInheritance()");
      org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("thag").build();
      org_jboss_resteasy_example___CC2 cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(17).setCC3Super(cc3).build();
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/inheritance").setOrgJbossResteasyExampleCC2Field(cc2);
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      org_jboss_resteasy_example___CC2 response;
      try {
         response = blockingStub.inheritance(gem);
         System.out.println("response: " + response.toString());
         cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("xthagy").build();
         cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(18).setCC3Super(cc3).build();
         Assert.assertTrue(cc2.equals(response));
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testReferenceField() throws Exception {
      System.out.println("running testReferenceField()");
      org_jboss_resteasy_example___CC5 cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(11).build();
      org_jboss_resteasy_example___CC4 cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("grog").setCc5(cc5).build();
      GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
      messageBuilder.setURL("http://localhost:8080/p/reference").setOrgJbossResteasyExampleCC4Field(cc4);
      GeneralEntityMessage gem = messageBuilder.build();
      System.out.println("gem: " + gem);
      org_jboss_resteasy_example___CC4 response;
      try {
         response = blockingStub.referenceField(gem);
         System.out.println("response: " + response.toString());
         cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(12).build();
         cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("xgrogy").setCc5(cc5).build();
         Assert.assertTrue(cc4.equals(response));
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }

   @Test
   public void testServletInfo() throws Exception {
      System.out.println("running testServletInfo()");
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
      System.out.println("gem: " + gem);
      try {
         gString response = blockingStub.testServletInfo(gem);
         System.out.println("response: " + response.toString());
         Assert.assertTrue("UTF-16|1.2.3.5|BLUEMONKEY|7778".equals(response.getValue()));
      } catch (StatusRuntimeException e) {
         e.printStackTrace();
         Assert.fail("fail");
         return;
      }
   }
}
