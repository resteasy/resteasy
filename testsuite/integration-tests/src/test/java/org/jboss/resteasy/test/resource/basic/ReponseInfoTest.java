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
import org.jboss.resteasy.test.resource.basic.resource.ReponseInfoResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReponseInfoTest {

    static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ReponseInfoTest.class.getSimpleName());
        war.addClasses(PortProviderUtil.class, ReponseInfoTest.class);

        // Use of PortProviderUtil in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ReponseInfoResource.class);
    }

    private void basicTest(String path) {
        WebTarget base = client.target(PortProviderUtil.generateURL(path, ReponseInfoTest.class.getSimpleName()));

        Response response = base.request().get();

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check URI location from HTTP headers from response prepared in resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfo() throws Exception {
        basicTest("/simple");
    }
}
