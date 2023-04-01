package org.jboss.resteasy.test.grpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.rest.example.CC1ServiceGrpc;
import jakarta.rest.example.CC1ServiceGrpc.CC1ServiceBlockingStub;
import jakarta.rest.example.CC1ServiceGrpc.CC1ServiceFutureStub;
import jakarta.rest.example.CC1ServiceGrpc.CC1ServiceStub;
import jakarta.rest.example.CC1_proto;
import jakarta.rest.example.CC1_proto.FormMap;
import jakarta.rest.example.CC1_proto.FormValues;
import jakarta.rest.example.CC1_proto.GeneralEntityMessage;
import jakarta.rest.example.CC1_proto.GeneralReturnMessage;
import jakarta.rest.example.CC1_proto.ServletInfo;
import jakarta.rest.example.CC1_proto.gCookie;
import jakarta.rest.example.CC1_proto.gHeader;
import jakarta.rest.example.CC1_proto.gInteger;
import jakarta.rest.example.CC1_proto.gNewCookie;
import jakarta.rest.example.CC1_proto.gString;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC2;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC3;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC4;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC5;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC7;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_example___CC9;
import jakarta.rest.example.CC1_proto.org_jboss_resteasy_grpc_sse_runtime___SseEvent;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
// import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Any;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Timestamp;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.TlsChannelCredentials;
import io.grpc.stub.StreamObserver;

/**
 * @tpSubChapter gRPC bridge plus WildFly grpc subsystem
 * @tpChapter grpc-tests tests
 * @tpSince RESTEasy 6.3.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GrpcToJakartaRESTTest {
    protected static final Logger log = Logger.getLogger(GrpcToJakartaRESTTest.class.getName());

    @Deployment(name = "jbossas-plaintext")
    @TargetsContainer("jbossas-plaintext")
    public static Archive<?> deployPlainText() {
        return doDeploy();
    }

    @Deployment(name = "jbossas-ssl-oneway")
    @TargetsContainer("jbossas-ssl-oneway")
    public static Archive<?> deploySslOneWay() {
        return doDeploy();
    }

    @Deployment(name = "jbossas-ssl-twoway")
    @TargetsContainer("jbossas-ssl-twoway")
    public static Archive<?> deploySslTwoWay() {
        return doDeploy();
    }

    public static Archive<?> doDeploy() {
        WebArchive war = TestUtil.prepareArchive(GrpcToJakartaRESTTest.class.getSimpleName());
        war.merge(ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtil.resolveDependency("jakarta.rest.example:jakarta.rest.example.grpc:war:0.0.39")));
        war.addClass(CC1ServiceGrpcImplSub.class);
        WebArchive archive = (WebArchive) TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
        // log.info(archive.toString(true));
        // archive.as(ZipExporter.class).exportTo(new File("/tmp/GrpcToJaxrs.jar"), true);
        return archive;
    }

    private static ManagedChannel channelPlaintext;
    private static ManagedChannel channelSslOneway;
    private static ManagedChannel channelSslTwoway;

    private static CC1ServiceBlockingStub blockingStubPlaintext;
    private static CC1ServiceBlockingStub blockingStubSslOneway;
    private static CC1ServiceBlockingStub blockingStubSslTwoway;

    private static CC1ServiceStub asyncStubPlaintext;
    private static CC1ServiceStub asyncStubSslOneway;
    private static CC1ServiceStub asyncStubSslTwoway;

    private static CC1ServiceFutureStub futureStubPlaintext;
    private static CC1ServiceFutureStub futureStubSslOneway;
    private static CC1ServiceFutureStub futureStubSslTwoway;

    @BeforeClass
    public static void beforeClass() throws Exception {
        accessServletContexts();
        ClassLoader classLoader = GrpcToJakartaRESTTest.class.getClassLoader();
        channelPlaintext = ManagedChannelBuilder.forTarget("localhost:9555").usePlaintext().build();
        {
            InputStream trustStore = classLoader.getResourceAsStream("client.truststore.pem");
            ChannelCredentials creds = TlsChannelCredentials.newBuilder().trustManager(trustStore).build();
            channelSslOneway = Grpc.newChannelBuilderForAddress("localhost", 10555, creds).build();
        }
        {
            InputStream trustStore = classLoader.getResourceAsStream("client.truststore.pem");
            InputStream keyStore = classLoader.getResourceAsStream("client.keystore.pem");
            InputStream key = classLoader.getResourceAsStream("client.key.pem");
            ChannelCredentials creds = TlsChannelCredentials.newBuilder().trustManager(trustStore).keyManager(keyStore, key)
                    .build();
            channelSslTwoway = Grpc.newChannelBuilderForAddress("localhost", 11555, creds).build();
        }

        blockingStubPlaintext = CC1ServiceGrpc.newBlockingStub(channelPlaintext);
        blockingStubSslOneway = CC1ServiceGrpc.newBlockingStub(channelSslOneway);
        blockingStubSslTwoway = CC1ServiceGrpc.newBlockingStub(channelSslTwoway);

        asyncStubPlaintext = CC1ServiceGrpc.newStub(channelPlaintext);
        asyncStubSslOneway = CC1ServiceGrpc.newStub(channelSslOneway);
        asyncStubSslTwoway = CC1ServiceGrpc.newStub(channelSslTwoway);

        futureStubPlaintext = CC1ServiceGrpc.newFutureStub(channelPlaintext);
        futureStubSslOneway = CC1ServiceGrpc.newFutureStub(channelSslOneway);
        futureStubSslTwoway = CC1ServiceGrpc.newFutureStub(channelSslTwoway);
    }

    static void accessServletContexts() {
        Client client = ClientBuilder.newClient();
        client.target("http://localhost:8080/GrpcToJakartaRESTTest/grpcToJakartaRest/grpcserver/context").request().get();
        client.target("http://localhost:9080/GrpcToJakartaRESTTest/grpcToJakartaRest/grpcserver/context").request().get();
        client.target("http://localhost:10080/GrpcToJakartaRESTTest/grpcToJakartaRest/grpcserver/context").request().get();
        client.close();
    }

    @AfterClass
    public static void afterClass() throws InterruptedException {
        channelPlaintext.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        channelSslOneway.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        channelSslTwoway.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    @OperateOnDeployment("jbossas-plaintext")
    public void testPlaintext() throws Exception {
        doBlockingTest(blockingStubPlaintext);
        doAsyncTest(asyncStubPlaintext);
        doFutureTest(futureStubPlaintext);
    }

    @Test
    @OperateOnDeployment("jbossas-ssl-oneway")
    public void testSslOneway() throws Exception {
        doBlockingTest(blockingStubSslOneway);
        doAsyncTest(asyncStubSslOneway);
        doFutureTest(futureStubSslOneway);
    }

    @Test
    @OperateOnDeployment("jbossas-ssl-twoway")
    public void testSslTwoway() throws Exception {
        doBlockingTest(blockingStubSslTwoway);
        doAsyncTest(asyncStubSslTwoway);
        doFutureTest(futureStubSslTwoway);
    }

    /****************************************************************************************/
    /****************************************************************************************/
    void doBlockingTest(CC1ServiceBlockingStub stub) throws Exception {
        this.testBoolean(stub);
        this.testBooleanWithUnnecessaryURL(stub);
        this.testBooleanWrapper(stub);
        this.testByte(stub);
        this.testByteWrapper(stub);
        this.testChar(stub);
        this.testCharacter(stub);
        this.testCompletionStage(stub);
        this.testConstructor(stub);
        this.testConsumes(stub);
        this.testCookieParams(stub);
        this.testDouble(stub);
        this.testDoubleWrapper(stub);
        this.testFloat(stub);
        this.testFloatWrapper(stub);
        this.testHeaderParams(stub);
        this.testInheritance(stub);
        this.testInnerClass(stub);
        this.testInt(stub);
        this.testInteger(stub);
        this.testJaxrsResponse(stub);
        this.testLocatorGet(stub);
        this.testLocatorPost(stub);
        this.testLong(stub);
        this.testLongWrapper(stub);
        this.testMatrixParams(stub);
        this.testParamsList(stub);
        this.testParamsSet(stub);
        this.testParamsSortedSet(stub);
        this.testPathParams(stub);
        this.testProduces(stub);
        this.testQueryParams(stub);
        this.testReferenceField(stub);
        this.testResponse(stub);
        this.testServerCookies(stub);
        this.testServerHeaders(stub);
        this.testServletConfigServletName(stub);
        this.testServletContextInitParam(stub);
        this.testServletContextPath(stub);
        this.testServletInfo(stub);
        this.testServletParams(stub);
        this.testServletPath(stub);
        this.testServletResponse(stub);
        this.testShort(stub);
        this.testShortWrapper(stub);
        this.testSSE(stub);
        this.testString(stub);
        this.testSuspend(stub);
        this.testCopy(stub);
    }

    void doAsyncTest(CC1ServiceStub asyncStub) throws Exception {
        testIntAsyncStub(asyncStub);
        testSseAsyncStub(asyncStub);
    }

    void doFutureTest(CC1ServiceFutureStub futureStub) throws Exception {
        testIntFutureStub(futureStub);
    }

    /****************************************************************************************/
    /****************************************************************************************/
    void testBoolean(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gBoolean n = jakarta.rest.example.CC1_proto.gBoolean.newBuilder().setValue(false)
                .build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGBooleanField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getBoolean(gem);
            jakarta.rest.example.CC1_proto.gBoolean expected = jakarta.rest.example.CC1_proto.gBoolean.newBuilder()
                    .setValue(true).build();
            Assert.assertEquals(expected, response.getGBooleanField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testBooleanWithUnnecessaryURL(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gBoolean n = jakarta.rest.example.CC1_proto.gBoolean.newBuilder().setValue(false)
                .build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/boolean").setGBooleanField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getBoolean(gem);
            jakarta.rest.example.CC1_proto.gBoolean expected = jakarta.rest.example.CC1_proto.gBoolean.newBuilder()
                    .setValue(true).build();
            Assert.assertEquals(expected, response.getGBooleanField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testBooleanWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gBoolean n = jakarta.rest.example.CC1_proto.gBoolean.newBuilder().setValue(false)
                .build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGBooleanField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getBooleanWrapper(gem);
            jakarta.rest.example.CC1_proto.gBoolean expected = jakarta.rest.example.CC1_proto.gBoolean.newBuilder()
                    .setValue(true).build();
            Assert.assertEquals(expected, response.getGBooleanField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testByte(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gByte n = jakarta.rest.example.CC1_proto.gByte.newBuilder().setValue(3).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGByteField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getByte(gem);
            jakarta.rest.example.CC1_proto.gByte expected = jakarta.rest.example.CC1_proto.gByte.newBuilder().setValue(4)
                    .build();
            Assert.assertEquals(expected, response.getGByteField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testByteWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gByte n = jakarta.rest.example.CC1_proto.gByte.newBuilder().setValue(7).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGByteField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getByteWrapper(gem);
            jakarta.rest.example.CC1_proto.gByte expected = jakarta.rest.example.CC1_proto.gByte.newBuilder().setValue(8)
                    .build();
            Assert.assertEquals(expected, response.getGByteField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testShort(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gShort n = jakarta.rest.example.CC1_proto.gShort.newBuilder().setValue(3).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGShortField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getShort(gem);
            jakarta.rest.example.CC1_proto.gShort expected = jakarta.rest.example.CC1_proto.gShort.newBuilder().setValue(4)
                    .build();
            Assert.assertEquals(expected, response.getGShortField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testShortWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gShort n = jakarta.rest.example.CC1_proto.gShort.newBuilder().setValue(7).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGShortField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getShortWrapper(gem);
            jakarta.rest.example.CC1_proto.gShort expected = jakarta.rest.example.CC1_proto.gShort.newBuilder().setValue(8)
                    .build();
            Assert.assertEquals(expected, response.getGShortField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testInt(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gInteger n = jakarta.rest.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGIntegerField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getInt(gem);
            jakarta.rest.example.CC1_proto.gInteger expected = jakarta.rest.example.CC1_proto.gInteger.newBuilder().setValue(4)
                    .build();
            Assert.assertEquals(expected, response.getGIntegerField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testInteger(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gInteger n = jakarta.rest.example.CC1_proto.gInteger.newBuilder().setValue(3).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setGIntegerField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getInteger(gem);
            jakarta.rest.example.CC1_proto.gInteger expected = jakarta.rest.example.CC1_proto.gInteger.newBuilder().setValue(4)
                    .build();
            Assert.assertEquals(expected, response.getGIntegerField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testLong(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gLong n = jakarta.rest.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/long").setGLongField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getLong(gem);
            jakarta.rest.example.CC1_proto.gLong expected = jakarta.rest.example.CC1_proto.gLong.newBuilder().setValue(4L)
                    .build();
            Assert.assertEquals(expected, response.getGLongField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testLongWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gLong n = jakarta.rest.example.CC1_proto.gLong.newBuilder().setValue(3L).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Long").setGLongField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getLongWrapper(gem);
            jakarta.rest.example.CC1_proto.gLong expected = jakarta.rest.example.CC1_proto.gLong.newBuilder().setValue(4L)
                    .build();
            Assert.assertEquals(expected, response.getGLongField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testFloat(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gFloat n = jakarta.rest.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/float").setGFloatField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getFloat(gem);
            jakarta.rest.example.CC1_proto.gFloat expected = jakarta.rest.example.CC1_proto.gFloat.newBuilder().setValue(4.0f)
                    .build();
            Assert.assertEquals(expected, response.getGFloatField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testFloatWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gFloat n = jakarta.rest.example.CC1_proto.gFloat.newBuilder().setValue(3.0f).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Float").setGFloatField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getFloat(gem);
            jakarta.rest.example.CC1_proto.gFloat expected = jakarta.rest.example.CC1_proto.gFloat.newBuilder().setValue(4.0f)
                    .build();
            Assert.assertEquals(expected, response.getGFloatField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testDouble(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gDouble n = jakarta.rest.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/double").setGDoubleField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getDouble(gem);
            jakarta.rest.example.CC1_proto.gDouble expected = jakarta.rest.example.CC1_proto.gDouble.newBuilder().setValue(4.0d)
                    .build();
            Assert.assertEquals(expected, response.getGDoubleField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testDoubleWrapper(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gDouble n = jakarta.rest.example.CC1_proto.gDouble.newBuilder().setValue(3.0d).build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Double").setGDoubleField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getDouble(gem);
            jakarta.rest.example.CC1_proto.gDouble expected = jakarta.rest.example.CC1_proto.gDouble.newBuilder().setValue(4.0d)
                    .build();
            Assert.assertEquals(expected, response.getGDoubleField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testChar(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gCharacter n = jakarta.rest.example.CC1_proto.gCharacter.newBuilder().setValue("a")
                .build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/char").setGCharacterField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getChar(gem);
            jakarta.rest.example.CC1_proto.gCharacter expected = jakarta.rest.example.CC1_proto.gCharacter.newBuilder()
                    .setValue("A").build();
            Assert.assertEquals(expected, response.getGCharacterField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testCharacter(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gCharacter n = jakarta.rest.example.CC1_proto.gCharacter.newBuilder().setValue("a")
                .build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/Character").setGCharacterField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getChar(gem);
            jakarta.rest.example.CC1_proto.gCharacter expected = jakarta.rest.example.CC1_proto.gCharacter.newBuilder()
                    .setValue("A").build();
            Assert.assertEquals(expected, response.getGCharacterField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testString(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.gString n = jakarta.rest.example.CC1_proto.gString.newBuilder().setValue("abc").build();
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/string").setGStringField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.getString(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("ABC").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testConstructor(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/constructor").build();
        GeneralReturnMessage response;
        try {
            response = stub.constructor(gem);
            org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("eight").build();
            org_jboss_resteasy_example___CC9.Builder cc9Builder = org_jboss_resteasy_example___CC9.newBuilder();
            org_jboss_resteasy_example___CC9 expected = cc9Builder.setBo(true)
                    .setBy((byte) 1)
                    .setS((short) 2)
                    .setI(3)
                    .setL(4L)
                    .setF(5.0f)
                    .setD(6.0d)
                    .setC('7')
                    .setCc3(cc3)
                    .build();
            Assert.assertEquals(expected, response.getOrgJbossResteasyExampleCC9Field());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testProduces(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/produces").build();
        GeneralReturnMessage response;
        try {
            response = stub.produces(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("produces").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testConsumes(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/consumes").build();
        GeneralReturnMessage response;
        try {
            response = stub.produces(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("consumes").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testPathParams(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/path/aa/param/bb").build();
        GeneralReturnMessage response;
        try {
            response = stub.pathParams(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("xaaybbz").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testQueryParams(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/query?q1=a&q2=b").build();
        GeneralReturnMessage response;
        try {
            response = stub.queryParams(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("xaybz").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testMatrixParams(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080/p/matrix;m1=a;m2=b/more;m3=c").build();
        GeneralReturnMessage response;
        try {
            response = stub.matrixParams(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("waxbycz").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    /**
     * Clarify treatment of cookies
     */
    void testCookieParams(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/cookieParams");
        gCookie.Builder cookieBuilder1 = gCookie.newBuilder();
        gCookie.Builder cookieBuilder2 = gCookie.newBuilder();
        gCookie cookie1 = cookieBuilder1.setName("c1").setValue("v1").setPath("a/b").setDomain("d1").build();
        gCookie cookie2 = cookieBuilder2.setName("c2").setValue("v2").build();
        messageBuilder.addCookies(cookie1).addCookies(cookie2);
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.cookieParams(gem);
            Assert.assertEquals("xc1=v1;d1,a/b,0yc2=v2;,,0z", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testHeaderParams(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
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
            response = stub.headerParams(gem);
            Assert.assertEquals("xv1.1yv2z", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testParamsList(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
        builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
        GeneralEntityMessage gem = builder
                .setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/list/pv2?q1=qv1&q1=qv2").build();
        GeneralReturnMessage response;
        try {
            response = stub.paramsList(gem);
            gString expected = gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testParamsSet(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
        builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/set/pv2?q1=qv1&q1=qv2")
                .build();
        GeneralReturnMessage response;
        try {
            response = stub.paramsSet(gem);
            gString expected = gString.newBuilder().setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testParamsSortedSet(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder builder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        builder.putHeaders("h1", gHeader.newBuilder().addValues("hv1").addValues("hv2").build());
        GeneralEntityMessage gem = builder
                .setURL("http://localhost:8080" + "/p/params;m1=mv1;m1=mv2/pv1/sortedset/pv2?q1=qv1&q1=qv2").build();
        GeneralReturnMessage response;
        try {
            response = stub.paramsSortedSet(gem);
            jakarta.rest.example.CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder()
                    .setValue("hv1hv2mv1mv2pv1pv2qv1qv2").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testResponse(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        try {
            GeneralReturnMessage response = stub.getResponse(gem);
            org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("cc7").build();
            org_jboss_resteasy_example___CC7 cc7 = org_jboss_resteasy_example___CC7.newBuilder().setM(11).setCC3Super(cc3)
                    .build();
            Any any = response.getGoogleProtobufAnyField();
            Assert.assertEquals(cc7, any.unpack(org_jboss_resteasy_example___CC7.class));
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testSuspend(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/suspend");
        GeneralEntityMessage gem = messageBuilder.build();
        try {
            GeneralReturnMessage response = stub.suspend(gem);
            Any any = response.getGoogleProtobufAnyField();
            gString gS = any.unpack(gString.class);
            String s = gS.getValue();
            Assert.assertEquals("suspend", s);
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testCompletionStage(CC1ServiceBlockingStub stub) throws Exception {
        jakarta.rest.example.CC1_proto.GeneralEntityMessage.Builder messageBuilder = jakarta.rest.example.CC1_proto.GeneralEntityMessage
                .newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/async/cs");
        GeneralEntityMessage gem = messageBuilder.build();
        try {
            GeneralReturnMessage response = stub.getResponseCompletionStage(gem);
            Assert.assertEquals("cs", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testServletContextPath(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.contextPath(gem);
            Assert.assertEquals("/GrpcToJakartaRESTTest", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testServletContextInitParam(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/servletContext");
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.servletContext(gem);
            Assert.assertEquals("/grpcToJakartaRest", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testServletConfigServletName(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/servletConfig");
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.servletConfig(gem);
            Assert.assertEquals("CC1Servlet", response.getGStringField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testSSE(CC1ServiceBlockingStub stub) throws Exception {
        CC1_proto.GeneralEntityMessage.Builder messageBuilder = CC1_proto.GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/sse");
        GeneralEntityMessage gem = messageBuilder.build();
        Iterator<org_jboss_resteasy_grpc_sse_runtime___SseEvent> response;
        try {
            response = stub.sse(gem);
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
        ArrayList<org_jboss_resteasy_grpc_sse_runtime___SseEvent> list = new ArrayList<org_jboss_resteasy_grpc_sse_runtime___SseEvent>();
        while (response.hasNext()) {
            org_jboss_resteasy_grpc_sse_runtime___SseEvent sseEvent = response.next();
            list.add(sseEvent);
        }
        Assert.assertEquals(4, list.size());
        for (int k = 0; k < 3; k++) {
            org_jboss_resteasy_grpc_sse_runtime___SseEvent sseEvent = list.get(k);
            Assert.assertEquals("name" + (k + 1), sseEvent.getName());
            Any any = sseEvent.getData();
            gString gString = any.unpack(gString.class);
            Assert.assertEquals("event" + (k + 1), gString.getValue());
        }
        org_jboss_resteasy_grpc_sse_runtime___SseEvent sseEvent = list.get(3);
        Assert.assertEquals("name4", sseEvent.getName());
        Any any = sseEvent.getData();
        org_jboss_resteasy_example___CC5 cc5 = any.unpack(org_jboss_resteasy_example___CC5.class);
        Assert.assertEquals(org_jboss_resteasy_example___CC5.newBuilder().setK(4).build(), cc5);
    }

    void testInheritance(CC1ServiceBlockingStub stub) throws Exception {
        org_jboss_resteasy_example___CC3 cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("thag").build();
        org_jboss_resteasy_example___CC2 cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(17).setCC3Super(cc3).build();
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/inheritance").setOrgJbossResteasyExampleCC2Field(cc2);
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.inheritance(gem);
            cc3 = org_jboss_resteasy_example___CC3.newBuilder().setS("xthagy").build();
            cc2 = org_jboss_resteasy_example___CC2.newBuilder().setJ(18).setCC3Super(cc3).build();
            Assert.assertTrue(cc2.equals(response.getOrgJbossResteasyExampleCC2Field()));
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testReferenceField(CC1ServiceBlockingStub stub) throws Exception {
        org_jboss_resteasy_example___CC5 cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(11).build();
        org_jboss_resteasy_example___CC4 cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("grog").setCc5(cc5).build();
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("http://localhost:8080/p/reference").setOrgJbossResteasyExampleCC4Field(cc4);
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.referenceField(gem);
            cc5 = org_jboss_resteasy_example___CC5.newBuilder().setK(12).build();
            cc4 = org_jboss_resteasy_example___CC4.newBuilder().setS("xgrogy").setCc5(cc5).build();
            Assert.assertTrue(cc4.equals(response.getOrgJbossResteasyExampleCC4Field()));
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testServletInfo(CC1ServiceBlockingStub stub) throws Exception {
        ServletInfo servletInfo = ServletInfo.newBuilder()
                .setCharacterEncoding("utf-16")
                .setClientAddress("1.2.3.4")
                .setClientHost("bluemonkey")
                .setClientPort(7777).build();
        gString gstring = gString.newBuilder().setValue("servletInfo").build();
        GeneralEntityMessage gem = GeneralEntityMessage.newBuilder()
                .setURL("http://localhost:8080/p/servletInfo")
                .setServletInfo(servletInfo)
                .setGStringField(gstring).build();
        try {
            GeneralReturnMessage response = stub.testServletInfo(gem);
            Assert.assertTrue("UTF-16|1.2.3.5|BLUEMONKEY|7778".equals(response.getGStringField().getValue()));
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    /**
     * Clarify treatment of cookies
     */
    void testServerCookies(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.serverCookies(gem);
            List<gNewCookie> list = response.getCookiesList();
            Assert.assertEquals(2, list.size());
            gNewCookie c1 = gNewCookie.newBuilder().setDomain("d1").setMaxAge(-1).setName("n1").setPath("p1").setValue("v1")
                    .build();
            gNewCookie c2 = gNewCookie.newBuilder().setDomain("d2").setMaxAge(17).setName("n2").setPath("p2").setValue("v2")
                    .setHttpOnly(true).setSecure(true).build();
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

    void testServerHeaders(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.serverHeaders(gem);
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

    void testServletPath(CC1ServiceBlockingStub stub) throws Exception {
        String contextPath;
        {
            GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
            GeneralEntityMessage gem = messageBuilder.build();
            GeneralReturnMessage response;
            try {
                response = stub.servletPath(gem);
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
            GeneralEntityMessage gem = messageBuilder
                    .setURL("http://localhost:8080" + contextPath + "/grpcToJakartaRest/p/servletPath").build();
            GeneralReturnMessage response;
            try {
                response = blockingStubPlaintext.servletPath(gem);
                String result = response.getGStringField().getValue();

                // context path
                int i = result.indexOf('|');
                Assert.assertEquals(contextPath, result.substring(0, i));

                // servlet path
                int j = result.indexOf('|', i + 1);
                Assert.assertEquals("/grpcToJakartaRest", result.substring(i + 1, j));

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

    void testServletParams(CC1ServiceBlockingStub stub) throws Exception {
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
            response = stub.servletParams(gem);
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
    void testJaxrsResponse(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.jaxrsResponse(gem);
            Assert.assertEquals(2, response.getCookiesCount());
            gNewCookie expectedCookie1 = gNewCookie.newBuilder().setDomain("d1").setName("n1").setPath("p1").setValue("v1")
                    .setMaxAge(11).setExpiry(Timestamp.newBuilder().setSeconds(111)).setHttpOnly(true).setVersion(1).build();
            gNewCookie expectedCookie2 = gNewCookie.newBuilder().setDomain("d2").setName("n2").setPath("p2").setValue("v2")
                    .setMaxAge(17).setExpiry(Timestamp.newBuilder().setSeconds(222)).setSecure(true).setVersion(1).build();
            Assert.assertTrue(expectedCookie1.equals(response.getCookies(0)) && expectedCookie2.equals(response.getCookies(1))
                    || expectedCookie1.equals(response.getCookies(1)) && expectedCookie2.equals(response.getCookies(0)));
            Map<String, CC1_proto.gHeader> headers = response.getHeadersMap();
            Assert.assertEquals(1, headers.get("h1").getValuesCount());
            Assert.assertEquals("v1", headers.get("h1").getValues(0));
            Assert.assertEquals(222, response.getStatus().getValue());
            Assert.assertEquals(1, headers.get("Content-Type").getValuesCount());
            Assert.assertEquals("x/y", headers.get("Content-Type").getValues(0));
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testServletResponse(CC1ServiceBlockingStub stub) throws Exception {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.servletResponse(gem);
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
            Assert.assertEquals("v1", headers.get("h1").getValues(0));
            Assert.assertEquals("13", headers.get("i1").getValues(0));

            Assert.assertTrue(headers.get("d2").getValues(0).contains("03 Jan 1970"));
            Assert.assertTrue(headers.get("d2").getValues(1).contains("04 Jan 1970"));
            Assert.assertEquals("v2a", headers.get("h2").getValues(0));
            Assert.assertEquals("v2b", headers.get("h2").getValues(1));
            Assert.assertEquals("19", headers.get("i2").getValues(0));
            Assert.assertEquals("29", headers.get("i2").getValues(1));

            Assert.assertTrue(headers.get("d3").getValues(0).contains("06 Jan 1970"));
            Assert.assertEquals("v3b", headers.get("h3").getValues(0));
            Assert.assertEquals("41", headers.get("i3").getValues(0));

            Assert.assertEquals(1, response.getCookiesCount());
            gNewCookie expectedCookie = gNewCookie.newBuilder().setDomain("d1").setMaxAge(3).setName("n1").setPath("p1")
                    .setValue("v1").build();
            Assert.assertEquals(expectedCookie, response.getCookies(0));

            Assert.assertEquals(223, response.getStatus().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail 2");
            return;
        }
    }

    void testInnerClass(CC1ServiceBlockingStub stub) {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.inner(gem);
            CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass.Builder builder = CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass
                    .newBuilder();
            CC1_proto.org_jboss_resteasy_example_CC1_INNER_InnerClass inner = builder.setI(3).setS("three").build();
            Assert.assertEquals(inner, response.getOrgJbossResteasyExampleCC1INNERInnerClassField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testLocatorGet(CC1ServiceBlockingStub stub) {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("/p/locator/get").setHttpMethod("GET");
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.locator(gem);
            Assert.assertEquals("/p/locator/get",
                    response.getGoogleProtobufAnyField().unpack(CC1_proto.gString.class).getValue());
        } catch (Exception e) {
            Assert.fail("fail");
            return;
        }
    }

    void testLocatorPost(CC1ServiceBlockingStub stub) {
        GeneralEntityMessage.Builder messageBuilder = GeneralEntityMessage.newBuilder();
        messageBuilder.setURL("/p/locator/post/abc").setHttpMethod("POST");
        messageBuilder.setGoogleProtobufAnyField(Any.pack(gString.newBuilder().setValue("xyz").build()));
        GeneralEntityMessage gem = messageBuilder.build();
        GeneralReturnMessage response;
        try {
            response = stub.locator(gem);
            Assert.assertEquals("abc|xyz", response.getGoogleProtobufAnyField().unpack(CC1_proto.gString.class).getValue());
        } catch (Exception e) {
            Assert.fail("fail");
            return;
        }
    }

    void testCopy(CC1ServiceBlockingStub stub) {
        CC1_proto.gString n = CC1_proto.gString.newBuilder().setValue("abc").build();
        CC1_proto.GeneralEntityMessage.Builder builder = CC1_proto.GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = builder.setURL("http://localhost:8080" + "/p/copy").setGStringField(n).build();
        GeneralReturnMessage response;
        try {
            response = stub.copy(gem);
            CC1_proto.gString expected = jakarta.rest.example.CC1_proto.gString.newBuilder().setValue("xyz").build();
            Assert.assertEquals(expected, response.getGStringField());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    static class GeneralReturnMessageHolder<T> {
        ArrayList<T> values = new ArrayList<T>();

        T getValue() {
            return values.get(0);
        }

        void setValue(T value) {
            values.add(value);
        }

        void addValue(T value) {
            values.add(value);
        }

        Iterator<T> iterator() {
            return values.iterator();
        }

        int size() {
            return values.size();
        }
    }

    void testIntAsyncStub(CC1ServiceStub asyncStub) throws Exception {
        gInteger n = gInteger.newBuilder().setValue(3).build();
        GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = builder.setGIntegerField(n).build();
        CountDownLatch latch = new CountDownLatch(1);
        GeneralReturnMessageHolder<Integer> grmh = new GeneralReturnMessageHolder<Integer>();
        StreamObserver<GeneralReturnMessage> responseObserver = new StreamObserver<GeneralReturnMessage>() {
            @Override
            public void onNext(GeneralReturnMessage value) {
                grmh.setValue(value.getGIntegerField().getValue());
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
        try {
            asyncStub.getInt(gem, responseObserver);
            latch.await();
            Assert.assertEquals((Integer) 4, grmh.getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testSseAsyncStub(CC1ServiceStub asyncStub) throws Exception {
        GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = builder.build();
        CountDownLatch latch = new CountDownLatch(1);
        GeneralReturnMessageHolder<org_jboss_resteasy_grpc_sse_runtime___SseEvent> grmh = new GeneralReturnMessageHolder<org_jboss_resteasy_grpc_sse_runtime___SseEvent>();
        StreamObserver<org_jboss_resteasy_grpc_sse_runtime___SseEvent> responseObserver = new StreamObserver<org_jboss_resteasy_grpc_sse_runtime___SseEvent>() {
            @Override
            public void onNext(org_jboss_resteasy_grpc_sse_runtime___SseEvent value) {
                grmh.addValue(value);
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
        try {
            asyncStub.sse(gem, responseObserver);
            latch.await();
            Assert.assertEquals(4, grmh.size());
            Iterator<org_jboss_resteasy_grpc_sse_runtime___SseEvent> it = grmh.iterator();
            for (int i = 0; i < 3; i++) {
                org_jboss_resteasy_grpc_sse_runtime___SseEvent sseEvent = it.next();
                Assert.assertEquals("name" + (i + 1), sseEvent.getName());
                byte[] bytes = sseEvent.getData().toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                Any any = Any.parseFrom(CodedInputStream.newInstance(bais));
                gString gString = any.unpack(gString.class);
                Assert.assertEquals("event" + (i + 1), gString.getValue());
            }
            org_jboss_resteasy_grpc_sse_runtime___SseEvent sseEvent = it.next();
            Assert.assertEquals("name4", sseEvent.getName());
            Any any = sseEvent.getData();
            org_jboss_resteasy_example___CC5 cc5 = any.unpack(org_jboss_resteasy_example___CC5.class);
            Assert.assertEquals(org_jboss_resteasy_example___CC5.newBuilder().setK(4).build(), cc5);
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }

    void testIntFutureStub(CC1ServiceFutureStub futureStub) throws Exception {
        gInteger n = gInteger.newBuilder().setValue(3).build();
        GeneralEntityMessage.Builder builder = GeneralEntityMessage.newBuilder();
        GeneralEntityMessage gem = builder.setGIntegerField(n).build();
        try {
            ListenableFuture<GeneralReturnMessage> future = futureStub.getInt(gem);
            Assert.assertEquals(4, future.get().getGIntegerField().getValue());
        } catch (StatusRuntimeException e) {
            Assert.fail("fail");
            return;
        }
    }
}
