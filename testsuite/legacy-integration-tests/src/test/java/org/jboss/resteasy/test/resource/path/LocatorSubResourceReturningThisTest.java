package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.LocatorSubResourceReturningThisParamEntityPrototype;
import org.jboss.resteasy.test.resource.path.resource.LocatorSubResourceReturningThisParamEntityWithConstructor;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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
        war.addClasses(LocatorSubResourceReturningThisPathParamTest.class, LocatorSubResourceReturningThisParamEntityPrototype.class,
                LocatorSubResourceReturningThisParamEntityWithConstructor.class);
        return TestUtil.finishContainerPrepare(war, null, LocatorSubResourceReturningThisSubResource.class);
    }

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request for the resource Locator, which returns itself. The Resource Locator here
     * extends the resource with HTTP methods annotations directly.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void LocatorWithSubWithPathAnnotationTest() {
        Response response = client.target(PortProviderUtil.generateURL("/resource/subresource/ParamEntityWithConstructor/ParamEntityWithConstructor=JAXRS", LocatorSubResourceReturningThisTest.class.getSimpleName())).request().get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }

}
