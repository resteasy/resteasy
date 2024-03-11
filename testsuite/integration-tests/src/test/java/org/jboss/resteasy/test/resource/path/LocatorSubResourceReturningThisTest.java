package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.LocatorSubResourceReturningThisParamEntityPrototype;
import org.jboss.resteasy.test.resource.path.resource.LocatorSubResourceReturningThisParamEntityWithConstructor;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class LocatorSubResourceReturningThisTest {

    @Path("resource")
    public static class LocatorSubResourceReturningThisSubResource extends LocatorSubResourceReturningThisPathParamTest {

        @Path("subresource")
        public LocatorSubResourceReturningThisSubResource subresorce() {
            return this;
        }
    }

    @Path(value = "/PathParamTest")
    public static class LocatorSubResourceReturningThisPathParamTest {

        @Produces(MediaType.TEXT_PLAIN)
        @GET
        @Path("/ParamEntityWithConstructor/{id}")
        public String paramEntityWithConstructorTest(
                @DefaultValue("PathParamTest") @PathParam("id") LocatorSubResourceReturningThisParamEntityWithConstructor paramEntityWithConstructor) {
            return paramEntityWithConstructor.getValue();
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(LocatorSubResourceReturningThisTest.class.getSimpleName());
        war.addClasses(LocatorSubResourceReturningThisPathParamTest.class,
                LocatorSubResourceReturningThisParamEntityPrototype.class,
                LocatorSubResourceReturningThisParamEntityWithConstructor.class);
        return TestUtil.finishContainerPrepare(war, null, LocatorSubResourceReturningThisSubResource.class);
    }

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request for the resource Locator, which returns itself. The Resource Locator here
     *                extends the resource with HTTP methods annotations directly.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void LocatorWithSubWithPathAnnotationTest() {
        Response response = client.target(PortProviderUtil.generateURL(
                "/resource/subresource/ParamEntityWithConstructor/ParamEntityWithConstructor=JAXRS",
                LocatorSubResourceReturningThisTest.class.getSimpleName())).request().get();
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }

}
