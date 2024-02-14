package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.param.resource.QueryParamWithMultipleEqualsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test query params with multiple equals
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class QueryParamWithMultipleEqualsTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(QueryParamWithMultipleEqualsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, QueryParamWithMultipleEqualsResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, QueryParamWithMultipleEqualsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test query parameter "foo=weird=but=valid"
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParam() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(generateURL("/test?foo=weird=but=valid")).request().get();

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assertions.assertEquals("weird=but=valid", entity,
                "Wrong content of response");

        response.close();
        client.close();
    }
}
