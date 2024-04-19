package org.jboss.resteasy.test.resource.basic;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.resource.basic.resource.MultipleGetResource;
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
 * Verify that setting resteasy config flag, resteasy_fail_fast to 'true' causes
 * resteasy to report error and not warning.
 * This feature is provided for quarkus.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipleGetResourceTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MultipleGetResourceTest.class.getSimpleName());
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_FAIL_FAST_ON_MULTIPLE_RESOURCES_MATCHING, "true");
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new FilePermission(TestUtil.getStandaloneDir(DEFAULT_CONTAINER_QUALIFIER) + File.separator + "log" +
                        File.separator + "server.log", "read"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("jboss.home.dir", "read"),
                new PropertyPermission("jboss.server.base.dir", "read"),
                new RuntimePermission("accessDeclaredMembers")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, contextParam, MultipleGetResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultipleGetResourceTest.class.getSimpleName());
    }

    @Test
    public void testFailFast() throws Exception {

        WebTarget base = client.target(generateURL("/api"));
        try (Response response = base.request().get()) {
            Assertions.assertEquals(500, response.getStatus());
            final String error = response.readEntity(String.class);
            Assertions.assertTrue(error.contains("RESTEASY005042"), "RESTEASY005042 not found in: " + error);
        }
    }
}
