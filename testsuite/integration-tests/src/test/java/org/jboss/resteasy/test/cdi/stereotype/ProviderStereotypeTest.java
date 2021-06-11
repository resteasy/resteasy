package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.Dummy;
import org.jboss.resteasy.test.cdi.stereotype.resource.DummyProviderResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.DummyProviderStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.TestApplication;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProviderStereotype;
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
 * @tpTestCaseDetails Tests @Provider annotation on stereotype. Test verifies that @Provider resource is invoked.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProviderStereotypeTest {

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
        WebArchive war = TestUtil.prepareArchive(ProviderStereotypeTest.class.getSimpleName());
        war.addClasses(TestApplication.class, ProviderStereotype.class, Dummy.class, DummyProviderResource.class, DummyProviderStereotypeResource.class);
        return war;
    }

    @Test
    public void testProviderStereotype(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/provider", ProviderStereotypeTest.class.getSimpleName()));
        Response response = base.path("get").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Dummy provider", response.readEntity(String.class));
    }
}
