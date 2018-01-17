package org.jboss.resteasy.test.resource.path;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.PathParamMissingDefaultValueBeanParamEntity;
import org.jboss.resteasy.test.resource.path.resource.PathParamMissingDefaultValueResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.5
 * @tpTestCaseDetails Check for slash in URL
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PathParamMissingDefaultValueTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathParamMissingDefaultValueTest.class.getSimpleName());
        war.addClass(PathParamMissingDefaultValueBeanParamEntity.class);
        return TestUtil.finishContainerPrepare(war, null, PathParamMissingDefaultValueResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PathParamMissingDefaultValueTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Missing @PathParam in BeanParam with no @DefaultValue should get java default value.
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testTrailingSlash() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/resource/test/")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Wrong response", "nullnullnullnullnullnull", response.readEntity(String.class));
        client.close();
    }
}