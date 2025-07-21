package org.jboss.resteasy.test.providers.jackson2;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.resource.CustomJackson2ProviderApplication;
import org.jboss.resteasy.test.providers.jackson2.resource.CustomJackson2ProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.23
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CustomJackson2ProviderTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CustomJackson2ProviderTest.class.getSimpleName());
        war.addClasses(CustomJackson2ProviderApplication.class, CustomJackson2ProviderResource.class);
        war.addAsWebInfResource(CustomJackson2ProviderTest.class.getPackage(), "jboss-deployment-structure-exclude-jaxrs.xml",
                "jboss-deployment-structure.xml");
        final PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        war.addAsLibraries(resolver.resolve(
                "org.jboss.resteasy:resteasy-servlet-initializer",
                "org.jboss.resteasy:resteasy-core",
                "org.jboss.resteasy:resteasy-core-spi",
                "org.jboss.resteasy:resteasy-jackson2-provider").withTransitivity().asFile());
        return war;
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomJackson2ProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Deployment contains jboss-deployment-structure.xml, which excludes jaxrs subsystem and brings its
     *                own version of resteasy libraries (resteasy-servlet-initializer, resteasy-jaxrs,
     *                resteasy-jackson2-provider and it's dependencies).
     *                Test verifies that Jackson2Provider from .war archive was loaded instead of the container default one.
     * @tpPassCrit The resource returns Success response
     * @tpSince RESTEasy 3.0.23
     */
    @Test
    public void testCustomUsed() {
        WebTarget target = client.target(generateURL("/jackson2providerpath"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(
                entity.contains(
                        CustomJackson2ProviderTest.class.getSimpleName() + ".war/WEB-INF/lib/resteasy-jackson2-provider"),
                "Jackson2Provider jar was loaded from unexpected source");
    }

}
