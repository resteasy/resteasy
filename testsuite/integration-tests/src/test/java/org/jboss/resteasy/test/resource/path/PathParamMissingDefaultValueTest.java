package org.jboss.resteasy.test.resource.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.PathParamMissingDefaultValueBeanParamEntity;
import org.jboss.resteasy.test.resource.path.resource.PathParamMissingDefaultValueResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 * @tpTestCaseDetails Check for slash in URL
 */
@ExtendWith(ArquillianExtension.class)
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
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testTrailingSlash() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/resource/test/")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals("nullnullnullnullnullnull", response.readEntity(String.class),
                "Wrong response");
        client.close();
    }
}
