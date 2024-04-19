package org.jboss.resteasy.test.asynch;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.security.SecurityPermission;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.asynch.resource.AsyncPostProcessingInterceptor;
import org.jboss.resteasy.test.asynch.resource.AsyncPostProcessingMsgBodyWriterInterceptor;
import org.jboss.resteasy.test.asynch.resource.AsyncPostProcessingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-767
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class AsyncPostProcessingTest {

    private static Logger logger = Logger.getLogger(AsyncPostProcessingTest.class);
    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(AsyncPostProcessingTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addAsWebInfResource(AsyncPostProcessingTest.class.getPackage(), "AsyncPostProcessingTestWeb.xml", "web.xml");
        // Arquillian in the deployment
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SecurityPermission("insertProvider"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, AsyncPostProcessingResource.class,
                AsyncPostProcessingMsgBodyWriterInterceptor.class, AsyncPostProcessingInterceptor.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncPostProcessingTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test synchronized request.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSync() throws Exception {
        reset();
        Response response = client.target(generateURL("/sync")).request().get();
        logger.info("Status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info("TestMessageBodyWriterInterceptor.called: " + AsyncPostProcessingMsgBodyWriterInterceptor.called);
        logger.info("TestPostProcessInterceptor.called: " + AsyncPostProcessingInterceptor.called);
        response.bufferEntity();
        logger.info("returned entity: " + response.readEntity(String.class));
        Assertions.assertTrue(AsyncPostProcessingMsgBodyWriterInterceptor.called,
                "AsyncPostProcessingMsgBodyWriterInterceptor interceptor was not called");
        Assertions.assertTrue(AsyncPostProcessingInterceptor.called,
                "AsyncPostProcessingInterceptor interceptor was not called");
        Assertions.assertEquals("sync", response.readEntity(String.class),
                "Entity has wrong content");
    }

    /**
     * @tpTestDetails Test async request with delay.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsyncWithDelay() throws Exception {
        reset();
        Response response = client.target(generateURL("/async/delay")).request().get();
        logger.info("Status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info("TestMessageBodyWriterInterceptor.called: " + AsyncPostProcessingMsgBodyWriterInterceptor.called);
        logger.info("TestPostProcessInterceptor.called: " + AsyncPostProcessingInterceptor.called);
        response.bufferEntity();
        logger.info("returned entity: " + response.readEntity(String.class));
        Assertions.assertTrue(AsyncPostProcessingMsgBodyWriterInterceptor.called,
                "AsyncPostProcessingMsgBodyWriterInterceptor interceptor was not called");
        Assertions.assertTrue(AsyncPostProcessingInterceptor.called,
                "AsyncPostProcessingInterceptor interceptor was not called");
        Assertions.assertEquals("async/delay", response.readEntity(String.class),
                "Entity has wrong content");
    }

    /**
     * @tpTestDetails Test async request without delay.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsyncWithNoDelay() throws Exception {
        reset();
        Response response = client.target(generateURL("/async/nodelay")).request().get();
        logger.info("Status: " + response.getStatus());
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info("TestMessageBodyWriterInterceptor.called: " + AsyncPostProcessingMsgBodyWriterInterceptor.called);
        logger.info("TestPostProcessInterceptor.called: " + AsyncPostProcessingInterceptor.called);
        response.bufferEntity();
        logger.info("returned entity: " + response.readEntity(String.class));
        Assertions.assertTrue(AsyncPostProcessingMsgBodyWriterInterceptor.called,
                "AsyncPostProcessingMsgBodyWriterInterceptor interceptor was not called");
        Assertions.assertTrue(AsyncPostProcessingInterceptor.called,
                "AsyncPostProcessingInterceptor interceptor was not called");
        Assertions.assertEquals("async/nodelay", response.readEntity(String.class),
                "Entity has wrong content");
    }

    private void reset() {
        AsyncPostProcessingMsgBodyWriterInterceptor.called = false;
        AsyncPostProcessingInterceptor.called = false;
    }
}
