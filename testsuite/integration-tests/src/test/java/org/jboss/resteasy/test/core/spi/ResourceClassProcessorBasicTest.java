package org.jboss.resteasy.test.core.spi;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.logging.Logger;
import org.jboss.resteasy.category.ExpectedFailing;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorEndPointCDI;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorEndPointEJB;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorClass;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorMethod;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorEndPoint;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorProxy;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorProxyEndPoint;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPointCDI;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPointEJB;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.AfterClass;
import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor SPI basic test, see RESTEASY-1805
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceClassProcessorBasicTest {

    static ResteasyClient client;

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorBasicTest.class.getName());

    // deployment names
    private static final String WAR_EJB = "war_with_ejb";
    private static final String WAR_CDI = "war_with_cdi";
    private static final String WAR_NORMAL = "war_normal";

    /**
     * Deployment with EJB end-points
     */
    @Deployment(name = WAR_EJB)
    public static Archive<?> deployEJB() {
        return deploy(WAR_EJB, ResourceClassProcessorEndPointEJB.class, ResourceClassProcessorPureEndPointEJB.class);
    }

    /**
     * Deployment with CDI end-points
     */
    @Deployment(name = WAR_CDI)
    public static Archive<?> deployCDI() {
        return deploy(WAR_CDI, ResourceClassProcessorEndPointCDI.class, ResourceClassProcessorPureEndPointCDI.class);
    }

    /**
     * Deployment with non-CDI && non-EJB end-points
     */
    @Deployment(name = WAR_NORMAL)
    public static Archive<?> deployNormla() {
        return deploy(WAR_NORMAL, ResourceClassProcessorEndPoint.class, ResourceClassProcessorPureEndPoint.class);
    }

    public static Archive<?> deploy(String name, Class<?> basicEndPoint, Class<?> pureEndPoint) {
        WebArchive war = TestUtil.prepareArchive(name);
        war.addClass(ResourceClassProcessorClass.class);
        war.addClass(ResourceClassProcessorMethod.class);
        war.addClass(ResourceClassProcessorProxy.class);

        return TestUtil.finishContainerPrepare(war, null,
                basicEndPoint, pureEndPoint,
                ResourceClassProcessorImplementation.class,
                ResourceClassProcessorProxyEndPoint.class
        );
    }

    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void after() {
        client.close();
    }

    /**
     * @tpTestDetails Test uses custom implementations of ResourceClassProcessor, ResourceClass and ResourceMethod
     *                Custom ResourceClassProcessor and ResourceClass should be used for end-point class
     *                Custom ResourceMethod should not affect tested end-point method, so end-point response should not be affected
     *                Deployment with non-CDI && non-EJB end-points is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassDefaultMethodTest() {
        customClassDefaultMethodTestHelper(WAR_NORMAL);
    }

    /**
     * @tpTestDetails Same as customClassDefaultMethodTest(), but CDI end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassDefaultMethodCdiTest() {
        customClassDefaultMethodTestHelper(WAR_CDI);
    }

    /**
     * @tpTestDetails Same as customClassDefaultMethodTest(), but EJB end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassDefaultMethodEjbTest() {
        customClassDefaultMethodTestHelper(WAR_EJB);
    }

    public void customClassDefaultMethodTestHelper(String warName) {
        Response response = client.target(PortProviderUtil.generateURL("/patched/pure", warName)).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat(response.getMediaType().toString(), containsString("text/plain"));
    }


    /**
     * @tpTestDetails Test uses custom implementations of ResourceClassProcessor, ResourceClass and ResourceMethod
     *                These custom implementations should rewrite produce media type of tested end-point
     *                Deployment with non-CDI && non-EJB end-points is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassCustomMethodTest() {
        customClassCustomMethodTestHelper(WAR_NORMAL);
    }

    /**
     * @tpTestDetails Same as customClassCustomMethodTest(), but CDI end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassCustomMethodCdiTest() {
        customClassCustomMethodTestHelper(WAR_CDI);
    }

    /**
     * @tpTestDetails Same as customClassCustomMethodTest(), but EJB end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customClassCustomMethodEjbTest() {
        customClassCustomMethodTestHelper(WAR_EJB);
    }

    public void customClassCustomMethodTestHelper(String warName) {
        Response response = client.target(PortProviderUtil.generateURL("/patched/custom", warName)).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat(response.getMediaType().toString(), containsString("application/xml"));
    }


    /**
     * @tpTestDetails Test uses custom implementations of ResourceClassProcessor, ResourceClass and ResourceMethod
     *                These implementations should not affect tested end-point class and end-point method
     *                End-point response should not be affected
     *                Deployment with non-CDI && non-EJB end-points is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void defaultClassDefaultMethodTest() {
        defaultClassDefaultMethodTestHelper(WAR_NORMAL);
    }

    /**
     * @tpTestDetails Same as defaultClassDefaultMethodTest(), but CDI end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void defaultClassDefaultMethodCdiTest() {
        defaultClassDefaultMethodTestHelper(WAR_CDI);
    }

    /**
     * @tpTestDetails Same as defaultClassDefaultMethodTest(), but EJB end-point is used
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void defaultClassDefaultMethodEjbTest() {
        defaultClassDefaultMethodTestHelper(WAR_EJB);
    }

    public void defaultClassDefaultMethodTestHelper(String warName) {
        Response response = client.target(PortProviderUtil.generateURL("/pure/pure", warName)).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat(response.getMediaType().toString(), containsString("text/plain"));
    }

    /**
     * @tpTestDetails Client doesn't use proxy, but end-point class doesn't contain jax-rs annotations
     *                Annotations are in interface, end-point implements this interface with jax-rs annotations
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void interfaceTest() {
        Response response = client.target(PortProviderUtil.generateURL("/proxy", WAR_NORMAL)).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat(response.getMediaType().toString(), containsString("application/xml"));
    }

    /**
     * @tpTestDetails Check ResourceClassProcessor support in client proxy
     * @tpSince RESTEasy 3.6
     */
    @Test
    @Category(ExpectedFailing.class)
    public void proxyTest() {
        ResteasyClient proxyClient= new ResteasyClientBuilder()
                .register(ResourceClassProcessorImplementation.class)
                .build();

        ResourceClassProcessorProxy proxy = proxyClient.target(PortProviderUtil.generateURL("/", WAR_NORMAL))
                                                .proxy(ResourceClassProcessorProxy.class);
        String response = proxy.custom();
        logger.info(String.format("Proxy response: %s", response));
        Assert.assertThat("Proxy returns wrong response", response, is("<a></a>"));

        proxyClient.close();
    }
}
