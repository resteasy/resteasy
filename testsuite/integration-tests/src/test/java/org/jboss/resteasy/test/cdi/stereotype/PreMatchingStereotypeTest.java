package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.PreMatchingStereotypeProvider;
import org.jboss.resteasy.test.cdi.stereotype.resource.PreMatchingStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.TestApplication;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.PreMatchingStereotype;
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
 * @tpTestCaseDetails Tests @PreMatching annotation on stereotype. Test verifies that @PreMatching provider is invoked.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PreMatchingStereotypeTest {
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
        WebArchive war = TestUtil.prepareArchive(PreMatchingStereotypeTest.class.getSimpleName());
        war.addClasses(TestApplication.class, PreMatchingStereotype.class, PreMatchingStereotypeResource.class, PreMatchingStereotypeProvider.class);
        return war;
    }

    @Test
    public void testPreMatchingStereotype(){
        WebTarget base = client.target(PortProviderUtil.generateURL("/hello/world", PreMatchingStereotypeTest.class.getSimpleName()));
        Response response = base.request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("preMatch success", response.readEntity(String.class));
    }
}
