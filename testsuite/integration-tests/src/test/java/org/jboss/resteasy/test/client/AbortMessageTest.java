package org.jboss.resteasy.test.client;

import java.io.UnsupportedEncodingException;
import java.util.logging.LoggingPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.resource.AbortMessageResourceFilter;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails RESTEASY-1540
 * @tpSince RESTEasy 3.1.0.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AbortMessageTest {
    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AbortMessageTest.class.getSimpleName());
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new LoggingPermission("control", ""),
                new RuntimePermission("accessDeclaredMembers")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, AbortMessageResourceFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AbortMessageTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Send response with "Aborted"
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testAbort() throws UnsupportedEncodingException {
        WebTarget target = client.target(generateURL("/showproblem"));
        Response response = target.request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("aborted", response.readEntity(String.class));
    }
}
