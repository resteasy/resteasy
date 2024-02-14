package org.jboss.resteasy.test.core.basic;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.core.basic.resource.InternalDispatcherClient;
import org.jboss.resteasy.test.core.basic.resource.InternalDispatcherForwardingResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for InternalDispatcher
 */
@ExtendWith(ArquillianExtension.class)
public class InternalDispatcherTest {
    private static Logger logger = Logger.getLogger(InternalDispatcherTest.class);

    private static InternalDispatcherForwardingResource forwardingResource;
    private static ResteasyClient client;
    public static final String PATH = "/foo/bar";

    @Deployment
    public static Archive<?> deploy() {

        WebArchive war = TestUtil.prepareArchive(InternalDispatcherTest.class.getSimpleName());
        war.addClasses(InternalDispatcherClient.class, InternalDispatcherForwardingResource.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("accessDeclaredMembers"),
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        List<Class<?>> singletons = new ArrayList<>();
        singletons.add(InternalDispatcherForwardingResource.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, singletons, (Class<?>[]) null);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(InternalDispatcherTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();

        Assertions.assertTrue(TestApplication.singletons.iterator().hasNext(),
                "No singleton founded");
        Object objectResource = TestApplication.singletons.iterator().next();
        Assertions.assertTrue(objectResource instanceof InternalDispatcherForwardingResource,
                "Wrong type of singleton founded");
        forwardingResource = (InternalDispatcherForwardingResource) objectResource;
        forwardingResource.uriStack.clear();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check response of forwarded reuests.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClientResponse() throws Exception {
        InternalDispatcherClient proxy = client.target(generateBaseUrl()).proxy(InternalDispatcherClient.class);

        Assertions.assertEquals("basic", proxy.getBasic(), "Wrong response");
        Assertions.assertEquals("basic", proxy.getForwardBasic(), "Wrong response");
        Assertions.assertEquals("object1", proxy.getObject(1).readEntity(String.class), "Wrong response");
        Assertions.assertEquals("object1", proxy.getForwardedObject(1).readEntity(String.class), "Wrong response");
        Response cr = proxy.getObject(0);
        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), cr.getStatus());
        cr.close();
        cr = proxy.getForwardedObject(0);
        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), cr.getStatus());
        cr.close();

        proxy.putForwardBasic("testBasic");
        Assertions.assertEquals("testBasic", proxy.getBasic(), "Wrong response");
        proxy.postForwardBasic("testBasic1");
        Assertions.assertEquals("testBasic1", proxy.getBasic(), "Wrong response");
        proxy.deleteForwardBasic();
        Assertions.assertEquals("basic", proxy.getBasic(), "Wrong response");

    }

    /**
     * @tpTestDetails assert that even though there were infinite forwards, there still was
     *                only 1 level of "context" data and that clean up occurred correctly.
     *                This should not spin forever, since RESTEasy stops the recursive loop
     *                after 20 internal dispatches
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInfinitForward() {
        InternalDispatcherClient proxy = client.target(generateBaseUrl()).proxy(InternalDispatcherClient.class);
        Assertions.assertEquals(1, proxy.infiniteForward(), "Cleanup was not correctly performed");
    }

    /**
     * @tpTestDetails Check UriInfo information without forwarding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfoBasic() {
        InternalDispatcherClient proxy = client.target(generateBaseUrl()).proxy(InternalDispatcherClient.class);
        proxy.getBasic();
        Assertions.assertEquals(generateBaseUrl() + "/basic",
                forwardingResource.uriStack.pop(),
                "Wrong UriInfo information without forwarding");
        Assertions.assertTrue(forwardingResource.uriStack.isEmpty(),
                "Wrong UriInfo information without forwarding");

    }

    /**
     * @tpTestDetails Check UriInfo information with forwarding. This is also regression test for JBEAP-2476
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfoForwardBasic() throws Exception {
        InternalDispatcherClient proxy = client.target(generateBaseUrl()).proxy(InternalDispatcherClient.class);

        logger.info("return value: " + proxy.getForwardBasic());
        int i = 0;
        for (Iterator<String> it = forwardingResource.uriStack.iterator(); it.hasNext(); i++) {
            logger.info(String.format("%d. item in uriStack: %s", i, it.next()));
        }

        Assertions.assertEquals(generateBaseUrl() + "/basic", forwardingResource.uriStack.pop(),
                "Wrong first URI in stack");
        Assertions.assertEquals(generateBaseUrl() + "/forward/basic",
                forwardingResource.uriStack.pop(),
                "Wrong second URI in stack");
        Assertions.assertTrue(forwardingResource.uriStack.isEmpty(),
                "Only two uri should be in stack");
    }

    /**
     * @tpTestDetails Regression test for JBEAP-2476
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfoForwardBasicComplexUri() {
        String baseUrl = generateBaseUrl();
        InternalDispatcherClient proxy = client.target(generateBaseUrl()).proxy(InternalDispatcherClient.class);

        proxy.getComplexForwardBasic();
        Assertions.assertEquals(baseUrl + PATH + "/basic", forwardingResource.uriStack.pop(),
                "Wrong first URI in stack");
        Assertions.assertEquals(baseUrl + PATH + "/forward/basic",
                forwardingResource.uriStack.pop(),
                "Wrong second URI in stack");
        Assertions.assertTrue(forwardingResource.uriStack.isEmpty(),
                "Only two uri should be in stack");
    }
}
