package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.EncodedStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.EncodedStereotype;
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
 * @tpTestCaseDetails Tests @Encoded annotation on stereotype. Test verifies that encoding on resource is respected.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EncodedStereotypeTest {

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
        WebArchive war = TestUtil.prepareArchive(EncodedStereotypeTest.class.getSimpleName());
        war.addClass(EncodedStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, EncodedStereotypeResource.class);
    }

    @Test
    public void testEncodedStereotype(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/hello%20world", EncodedStereotypeTest.class.getSimpleName()));
        Response response = base.request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("hello%20world", response.readEntity(String.class));
    }

}
