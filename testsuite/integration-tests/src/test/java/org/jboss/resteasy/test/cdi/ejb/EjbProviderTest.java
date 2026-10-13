package org.jboss.resteasy.test.cdi.ejb;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EjbProviderResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EjbSingletonProvider;
import org.jboss.resteasy.test.cdi.ejb.resource.EjbStatelessProvider;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that a stateless or singleton session bean provider which declares no scope is deployed. Such a session bean is
 * made {@code @Dependent}, which must not be rejected as an explicitly {@code @Dependent} provider.
 *
 * @since 8.0
 */
@ArquillianTest
@RunAsClient
public class EjbProviderTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, EjbProviderTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, EjbProviderResource.class, EjbStatelessProvider.class,
                        EjbSingletonProvider.class);
    }

    /**
     * The {@code @Stateless} provider is invoked.
     */
    @Test
    public void statelessProvider() throws Exception {
        assertProviderInvoked(EjbStatelessProvider.HEADER);
    }

    /**
     * The EJB {@code @Singleton} provider is invoked.
     */
    @Test
    public void singletonProvider() throws Exception {
        assertProviderInvoked(EjbSingletonProvider.HEADER);
    }

    private void assertProviderInvoked(final String header) throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(baseUri, "ejb-provider")).request().get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals("invoked", response.getHeaderString(header),
                    () -> "The provider for header " + header + " was not invoked");
        }
    }
}
