package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.ConstrainedToStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.DummyProviderResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ConstrainedToStereotype;
import org.jboss.resteasy.test.cdi.stereotype.resource.Dummy;
import org.jboss.resteasy.test.cdi.stereotype.resource.TestApplication;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @ConstrainedTo annotation on stereotype. Test verifies that the provider is not used as it is constrained to client.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ConstrainedToStereotypeTest {
    private static Client client;

    @BeforeClass
    public static void setup()
    {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    @Deployment
    private static Archive<?> deploy(){
        WebArchive war = TestUtil.prepareArchive(ConstrainedToStereotypeTest.class.getSimpleName());
        war.addClasses(TestApplication.class, ConstrainedToStereotype.class, Dummy.class, ConstrainedToStereotypeResource.class, DummyProviderResource.class);
        return war;
    }

    @Test
    public void testConstrainedToStereotype(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/provider", ConstrainedToStereotypeTest.class.getSimpleName()));
        Response response = base.path("get").request().get();

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        Assert.assertTrue(response.readEntity(String.class).contains(
                "Could not find MessageBodyWriter for response object of type"));
    }
}
