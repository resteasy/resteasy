package org.jboss.resteasy.test.microprofile.restclient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionActivator;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionResource;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionService;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionServiceIntf;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPPatronActivator;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show how to use injection to get access to the service.
 *                    Show configuration required for a GenericType return type.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MPClientCollectionTest {
    protected static final Logger LOG = Logger.getLogger(MPCollectionTest.class.getName());
    private static final String WAR_SERVICE = "war_service";
    private static final String WAR_CLIENT = "war_client";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(MPCollectionService.class,
                MPCollectionActivator.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    @Deployment(name=WAR_CLIENT)
    public static Archive<?> clientDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_CLIENT);
        war.addClasses(MPCollectionResource.class,
                MPCollectionServiceIntf.class,
                MPPatronActivator.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    static ResteasyClient client;
    @BeforeClass
    public static void before() throws Exception {
        client = (ResteasyClient)ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    @Test
    public void preTest() throws Exception {
        // pre-test, confirm the service is reachable
        // If this test fails the other tests will not pass
        Response response = client.target(
                generateURL("/theService/ping", WAR_SERVICE)).request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("pong", response.readEntity(String.class));
    }

    @Test
    public void testStringReturnType() throws Exception {

        // Test service is accessed via injection
        // Test endpoint with simple (String) return type
        Response response = client.target(
                generateURL("/thePatron/checking", WAR_CLIENT)).request().get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("pong thePatron", response.readEntity(String.class));
    }

    @Test
    public void testGenericTypeReturnType() throws Exception {
        // Test service is accessed via injection
        // Test endpoint with GenericType return type
        Response response = client.target(
                generateURL("/thePatron/got", WAR_CLIENT)).request().get();
        Assert.assertEquals(200, response.getStatus());
        List<String> l = response.readEntity(new GenericType<List<String>>() {});
        Assert.assertEquals(4, l.size());
        Assert.assertEquals("thePatron", l.get(3));
    }
}
