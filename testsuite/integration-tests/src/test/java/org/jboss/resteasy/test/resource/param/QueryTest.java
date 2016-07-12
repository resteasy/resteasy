package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.param.resource.QueryResource;
import org.jboss.resteasy.test.resource.param.resource.QuerySearchQuery;
import org.jboss.resteasy.util.HttpResponseCodes;
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
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for @Query param of the resource, RESTEASY-715
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class QueryTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(QueryTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, QueryResource.class, QuerySearchQuery.class);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, QueryTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Use resource with @Query annotation with the parameter of custom type which consist of @QueryParam fields.
     * Resteasy correctly parses the uri to get all specified parameters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamPrefix() throws Exception {
        WebTarget target = client.target(generateURL("/search?term=t1&order=ASC"));
        Response response = target.request().get();

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("term: 't1', order: 'ASC', limit: 'null'", response.readEntity(String.class));
    }
}
