package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.TwoStereotypesResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ConsumeStereotype;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProduceStereotype;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests two different @Produces and @Consumes stereotypes on the same resource class.
 * Test verifies that both @Produces and @Consumes media types are respected.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TwoStereotypesOnSameResourceTest {

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
        WebArchive war = TestUtil.prepareArchive(TwoStereotypesOnSameResourceTest.class.getSimpleName());
        war.addClasses(ConsumeStereotype.class, ProduceStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, TwoStereotypesResource.class);
    }

    @Test
    public void testProduces(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/stereotype", TwoStereotypesOnSameResourceTest.class.getSimpleName()));
        Response response = base.path("produces").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content of the response", "{}", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
    }

    @Test
    public void testConsumes(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/stereotype", TwoStereotypesOnSameResourceTest.class.getSimpleName()));
        Response response = base.path("consumes").request().header("Content-type", MediaType.APPLICATION_XML).post(Entity.xml("<>"));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
