package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.NoAnnotationResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.NoAnnotationStereotype;
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
 * @tpTestCaseDetails Tests standard behavior of stereotype without any JAX-RS annotations.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NoAnnotationStereotypeTest {
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
    private static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(NoAnnotationStereotypeTest.class.getSimpleName());
        war.addClasses(NoAnnotationStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, NoAnnotationResource.class);
    }

    @Test
    public void testStereotypeWithoutAnnotation(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/stereotype", NoAnnotationStereotypeTest.class.getSimpleName()));
        Response response = base.path("produces").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content of the response", "{}", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE, response.getMediaType());
    }
}
