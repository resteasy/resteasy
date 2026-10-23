package org.jboss.resteasy.test.cdi.modules;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesLibraryProvider;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesLibraryProviderResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that a provider in a WEB-INF/lib library is discovered by scanning. The library has no {@code beans.xml}, so
 * the provider is only a CDI bean because {@code @Provider} is a bean defining annotation.
 *
 * @since 8.0
 */
@ArquillianTest
@RunAsClient
public class WarLibProviderTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment
    public static WebArchive createTestArchive() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "library.jar")
                .addClasses(CDIModulesLibraryProvider.class);
        return ShrinkWrap.create(WebArchive.class, WarLibProviderTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, CDIModulesLibraryProviderResource.class)
                .addAsLibrary(jar);
    }

    /**
     * The provider in the library is discovered and its {@code @Context} field is injected.
     */
    @Test
    public void libraryProvider() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(baseUri, "/library-provider")).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String path = response.getHeaderString(CDIModulesLibraryProvider.HEADER);
            Assertions.assertNotNull(path, "The provider in the library was not invoked");
            Assertions.assertTrue(path.endsWith("/library-provider"), () -> "Unexpected request path " + path);
        }
    }
}
