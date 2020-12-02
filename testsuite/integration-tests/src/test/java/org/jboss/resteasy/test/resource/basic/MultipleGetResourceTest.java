package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.resource.basic.resource.MultipleGetResource;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * Verify that setting resteasy config flag, resteasy_fail_fast to 'true' causes
 * resteasy to report error and not warning.
 * This feature is provided for quarkus.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultipleGetResourceTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MultipleGetResourceTest.class.getSimpleName());
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_FAIL_FAST_ON_MULTIPLE_RESOURCES_MATCHING, "true");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new FilePermission(TestUtil.getStandaloneDir(DEFAULT_CONTAINER_QUALIFIER) + File.separator + "log" +
                        File.separator + "server.log", "read"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("jboss.home.dir", "read"),
                new PropertyPermission("jboss.server.base.dir", "read"),
                new RuntimePermission("accessDeclaredMembers")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, contextParam, MultipleGetResource.class);
    }

    @BeforeClass
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultipleGetResourceTest.class.getSimpleName());
    }

    @Test
    public void testFailFast() throws Exception {
        LogCounter errorStringLog = new LogCounter("RESTEASY005042",
                false, ContainerConstants.DEFAULT_CONTAINER_QUALIFIER);

        WebTarget base = client.target(generateURL("/api"));
        Response  response = base.request().get();
        Assert.assertEquals(500, response.getStatus());
        response.close();
        Assert.assertThat(errorStringLog.count(), is(2));
    }
}
