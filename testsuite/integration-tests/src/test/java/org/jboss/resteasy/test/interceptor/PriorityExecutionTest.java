package org.jboss.resteasy.test.interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientRequestFilter1;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientRequestFilter2;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientRequestFilter3;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientRequestFilterMax;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientRequestFilterMin;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientResponseFilter1;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientResponseFilter2;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientResponseFilter3;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientResponseFilterMax;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionClientResponseFilterMin;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerRequestFilter1;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerRequestFilter2;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerRequestFilter3;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerRequestFilterMax;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerRequestFilterMin;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerResponseFilter1;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerResponseFilter2;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerResponseFilter3;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerResponseFilterMax;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionContainerResponseFilterMin;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.LoggingPermission;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-1294
 */
@RunWith(Arquillian.class)
public class PriorityExecutionTest {
    public static volatile Queue<String> interceptors = new ConcurrentLinkedQueue<String>();
    public static Logger logger = Logger.getLogger(PriorityExecutionTest.class);
    private static final String WRONG_ORDER_ERROR_MSG = "Wrong order of interceptor execution";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PriorityExecutionTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addClasses(PriorityExecutionClientResponseFilterMin.class,
                PriorityExecutionClientResponseFilter1.class,
                PriorityExecutionClientRequestFilter2.class,
                PriorityExecutionClientRequestFilterMax.class,
                PriorityExecutionClientRequestFilter1.class,
                PriorityExecutionClientResponseFilter2.class,
                PriorityExecutionClientRequestFilter3.class,
                PriorityExecutionClientResponseFilter3.class,
                PriorityExecutionClientResponseFilterMax.class,
                PriorityExecutionClientRequestFilterMin.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")
        ), "permissions.xml");
        // finish preparation of war container, define end-point and filters
        return TestUtil.finishContainerPrepare(war, null,
                // end-point
                PriorityExecutionResource.class,
                // server filters
                PriorityExecutionContainerResponseFilter2.class,
                PriorityExecutionContainerResponseFilter1.class,
                PriorityExecutionContainerResponseFilter3.class,
                PriorityExecutionContainerResponseFilterMin.class,
                PriorityExecutionContainerResponseFilterMax.class,
                PriorityExecutionContainerRequestFilter2.class,
                PriorityExecutionContainerRequestFilter1.class,
                PriorityExecutionContainerRequestFilter3.class,
                PriorityExecutionContainerRequestFilterMin.class,
                PriorityExecutionContainerRequestFilterMax.class
        );
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PriorityExecutionTest.class.getSimpleName());
    }

    static Client client;

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @After
    public void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Check order of client and server filters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPriority() throws Exception {
        client.register(PriorityExecutionClientResponseFilter3.class);
        client.register(PriorityExecutionClientResponseFilter1.class);
        client.register(PriorityExecutionClientResponseFilter2.class);
        client.register(PriorityExecutionClientResponseFilterMin.class);
        client.register(PriorityExecutionClientResponseFilterMax.class);
        client.register(PriorityExecutionClientRequestFilter3.class);
        client.register(PriorityExecutionClientRequestFilter1.class);
        client.register(PriorityExecutionClientRequestFilter2.class);
        client.register(PriorityExecutionClientRequestFilterMin.class);
        client.register(PriorityExecutionClientRequestFilterMax.class);

        Response response = client.target(generateURL("/test")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", "test", response.getEntity());

        // client filters
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientRequestFilterMin", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientRequestFilter1", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientRequestFilter2", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientRequestFilter3", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientRequestFilterMax", interceptors.poll());

        // server filters
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerRequestFilterMin", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerRequestFilter1", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerRequestFilter2", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerRequestFilter3", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerRequestFilterMax", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerResponseFilterMax", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerResponseFilter3", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerResponseFilter2", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerResponseFilter1", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionContainerResponseFilterMin", interceptors.poll());

        // client filters
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientResponseFilterMax", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientResponseFilter3", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientResponseFilter2", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientResponseFilter1", interceptors.poll());
        Assert.assertEquals(WRONG_ORDER_ERROR_MSG, "PriorityExecutionClientResponseFilterMin", interceptors.poll());
    }
}
