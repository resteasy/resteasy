package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.PathStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.SecondPathStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.PathStereotype;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests whether @Path stereotype is not shared by two different resources on different deployments.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiplePathStereotypeTest {

    @ArquillianResource
    private Deployer deployer;

    private static final String FIRST_DEPLOY = "firstDeploy";
    private static final String SECOND_DEPLOY = "secondDeploy";

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

    @Deployment(name = FIRST_DEPLOY)
    private static Archive<?> firstDeploy()
    {
        WebArchive war = TestUtil.prepareArchive(FIRST_DEPLOY);
        war.addClasses(PathStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, PathStereotypeResource.class);
    }

    @Deployment(name = SECOND_DEPLOY, managed = false)
    private static Archive<?> secondDeploy()
    {
        WebArchive war = TestUtil.prepareArchive(SECOND_DEPLOY);
        war.addClasses(TestApplication.class, PathStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, SecondPathStereotypeResource.class);
    }

    @Test
    @OperateOnDeployment(FIRST_DEPLOY)
    public void testMultiplePathsStereotype(){
        deployer.deploy(SECOND_DEPLOY);

        WebTarget firstDeployment = client.target(PortProviderUtil.generateURL("/stereotype", FIRST_DEPLOY));
        Response firstResponse = firstDeployment.path("produces").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), firstResponse.getStatus());
        Assert.assertEquals("{}", firstResponse.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, firstResponse.getMediaType());

        WebTarget secondDeployment = client.target(PortProviderUtil.generateURL("/stereotype", SECOND_DEPLOY));
        Response secondResponse = secondDeployment.path("produces").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), secondResponse.getStatus());
        Assert.assertEquals("SecondPathStereotypeResource", secondResponse.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, secondResponse.getMediaType());

        deployer.undeploy(SECOND_DEPLOY);
    }
}
