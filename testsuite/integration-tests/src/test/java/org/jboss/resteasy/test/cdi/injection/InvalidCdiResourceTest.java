package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.injection.resource.AbstractCdiResource;
import org.jboss.resteasy.test.cdi.injection.resource.ConcreteCdiResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that an abstract root resource class in a bean archive with a {@code bean-discovery-mode} of {@code all} does
 * not prevent the other resources in the deployment from being served.
 *
 * @since 8.0
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha2")
public class InvalidCdiResourceTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, InvalidCdiResourceTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, AbstractCdiResource.class, ConcreteCdiResource.class);
    }

    /**
     * The concrete resource is served when an abstract root resource class is in the same deployment.
     */
    @Test
    public void concreteResource() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(baseUri, "concrete")).request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
            Assertions.assertEquals("concrete", response.readEntity(String.class));
        }
    }
}
