package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.QueryParamWithMultipleEqualsResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test query params with multiple equals
 */
@RunWith(Arquillian.class)
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
        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/test?foo=weird=but=valid")).request().get();

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals("Wrong content of response", "weird=but=valid", entity);

        response.close();
        client.close();
    }
}
