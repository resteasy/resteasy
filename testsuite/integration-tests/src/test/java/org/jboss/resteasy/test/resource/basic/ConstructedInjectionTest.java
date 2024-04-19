package org.jboss.resteasy.test.resource.basic;

import java.util.PropertyPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.config.security.ConfigPropertyPermission;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.test.resource.basic.resource.ConstructedInjectionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ConstructedInjectionTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ConstructedInjectionTest.class.getSimpleName());
        war.addClass(TestPortProvider.class);

        // Use of PortProviderUtil in the deployment
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                // Required for the PortProvider
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new ConfigPropertyPermission("RESTEASY_PORT"),
                new RuntimePermission("getenv.RESTEASY_HOST"),
                new ConfigPropertyPermission("RESTEASY_HOST"),
                new ConfigPropertyPermission("org.jboss.resteasy.host"),
                new PropertyPermission("org.jboss.resteasy.host", "read"),
                new RuntimePermission("getenv.org.jboss.resteasy.host", "read"),
                new ConfigPropertyPermission("org.jboss.resteasy.port"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new RuntimePermission("getenv.org.jboss.resteasy.port", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ConstructedInjectionResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ConstructedInjectionTest.class.getSimpleName());
    }

    private void _test(String path) {
        WebTarget base = client.target(generateURL(path));
        try {
            Response response = base.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Test with the resource containing custom constructor with @Context and @QueryParam injection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfo() throws Exception {
        _test("/simple");
    }

}
