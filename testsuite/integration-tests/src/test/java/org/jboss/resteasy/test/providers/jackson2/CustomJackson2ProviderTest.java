package org.jboss.resteasy.test.providers.jackson2;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.CustomJackson2ProviderResource;
import org.jboss.resteasy.test.providers.jackson2.resource.CustomJackson2ProviderApplication;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.maven.MavenUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import static org.hamcrest.CoreMatchers.containsString;


/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.23
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomJackson2ProviderTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CustomJackson2ProviderTest.class.getSimpleName());
        war.addClasses(CustomJackson2ProviderApplication.class, CustomJackson2ProviderResource.class);
        war.addAsWebInfResource(CustomJackson2ProviderTest.class.getPackage(), "jboss-deployment-structure-exclude-jaxrs.xml","jboss-deployment-structure.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("getProtectionDomain"),
                new ReflectPermission("suppressAccessChecks"),
                new PropertyPermission("resteasy.allowGzip", "read"),
                new PropertyPermission("resteasy.server.tracing.*", "read")
        ), "permissions.xml");
        MavenUtil mavenUtil;
        mavenUtil = MavenUtil.create(true);
        String version = System.getProperty("project.version");
        try {
            war.addAsLibraries(mavenUtil.createMavenGavRecursiveFiles("org.jboss.resteasy:resteasy-servlet-initializer:" + version).toArray(new File[]{}));
            war.addAsLibraries(mavenUtil.createMavenGavRecursiveFiles("org.jboss.resteasy:resteasy-jaxrs:" + version).toArray(new File[]{}));
            war.addAsLibraries(mavenUtil.createMavenGavRecursiveFiles("org.jboss.resteasy:resteasy-jackson2-provider:" + version).toArray(new File[]{}));

        } catch (Exception e) {
            throw new RuntimeException("Unable to get artifacts from maven via Aether library", e);
        }
        return war;
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomJackson2ProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Deployment contains jboss-deployment-structure.xml, which excludes jaxrs subsystem and brings its
     * own version of resteasy libraries (resteasy-servlet-initializer, resteasy-jaxrs, resteasy-jackson2-provider and it's dependencies).
     * Test verifies that Jackson2Provider from .war archive was loaded instead of the container default one.
     * @tpPassCrit The resource returns Success response
     * @tpSince RESTEasy 3.0.23
     */
    @Test
    public void testCustomUsed() {
        WebTarget target = client.target(generateURL("/jackson2providerpath"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat("Jackson2Provider jar was loaded from unexpected source",
                entity, containsString(CustomJackson2ProviderTest.class.getSimpleName() + ".war/WEB-INF/lib/resteasy-jackson2-provider"));
    }


}
