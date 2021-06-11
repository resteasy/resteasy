package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.DifferentStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ConsumeStereotype;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests whether method with stereotype annotation overrides stereotype's @Produces with its own.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DifferentMediaTypesStereotypeTest {
    private static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Deployment
    private static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DifferentMediaTypesStereotypeTest.class.getSimpleName());
        war.addClass(ConsumeStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, DifferentStereotypeResource.class);
    }
    @Test
    public void testDifferentMethodMediaTypes(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/stereotype", DifferentMediaTypesStereotypeTest.class.getSimpleName()));
        Response response = base.path("produces").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content of the response", "{}", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_ATOM_XML_TYPE, response.getMediaType());
    }
}
