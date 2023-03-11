package org.jboss.resteasy.test.core.spi;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorNotAppliedImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor should not be used in some case
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
public class ResourceClassProcessorNotAppliedTest {

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorNotAppliedTest.class.getName());

    private static List<String> visitedProcessors = new ArrayList<>();

    public static synchronized void addToVisitedProcessors(String item) {
        visitedProcessors.add(item);
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceClassProcessorNotAppliedTest.class.getSimpleName());
        war.addClass(ResourceClassProcessorNotAppliedTest.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(ResourceClassProcessorNotAppliedImplementation.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("arquillian.*", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null,
                ResourceClassProcessorPureEndPoint.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceClassProcessorNotAppliedTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails ResourceClassProcessor implementation should not be used if web.xml doesn't contains provider name
     *                and resteasy.scan is not allowed
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void notAppliedTest() {
        // init client
        client = (ResteasyClient) ClientBuilder.newClient();

        // do request
        Response response = client.target(generateURL("/pure/pure")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        // log visited processors
        int i = 0;
        for (String item : visitedProcessors) {
            logger.info(String.format("%d. %s", ++i, item));
        }

        // asserts
        MatcherAssert.assertThat("ResourceClassProcessor was used although it should not be used",
                visitedProcessors.size(), greaterThanOrEqualTo(0));

        // close client
        client.close();
    }
}
