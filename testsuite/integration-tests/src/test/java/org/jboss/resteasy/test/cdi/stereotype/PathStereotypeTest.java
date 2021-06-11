package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.PathMethodStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.PathStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.PathStereotype;
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
 * @tpTestCaseDetails Tests @Path annotation on stereotype. Test verifies that endpoints are exposed on both
 * the resource class and the method annotated with @Path stereotype.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PathStereotypeTest {

    private static final String DEPLOY_CLASS = "deployClass";
    private static final String DEPLOY_METHOD = "deployMethod";

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

    @Deployment(name = DEPLOY_CLASS)
    private static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(DEPLOY_CLASS);
        war.addClasses(PathStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, PathStereotypeResource.class);
    }

    @Deployment(name = DEPLOY_METHOD)
    private static Archive<?> deployMethod()
    {
        WebArchive war = TestUtil.prepareArchive(DEPLOY_METHOD);
        war.addClasses(PathStereotype.class);
        return TestUtil.finishContainerPrepare(war, null, PathMethodStereotypeResource.class);
    }

    @Test
    @OperateOnDeployment(DEPLOY_CLASS)
    public void testResourcePathStereotype(){
        checkResponse(getResponse(DEPLOY_CLASS));
    }

    @Test
    @OperateOnDeployment(DEPLOY_METHOD)
    public void testMethodPathStereotype(){
        checkResponse(getResponse(DEPLOY_METHOD));
    }

    private void checkResponse(Response response){
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("Wrong content of the response", "{}", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
    }

    private Response getResponse(String deployName){
        WebTarget base = client.target(PortProviderUtil.generateURL("/stereotype", deployName));
        Response response = base.path("produces").request().get();

        return response;
    }

}
