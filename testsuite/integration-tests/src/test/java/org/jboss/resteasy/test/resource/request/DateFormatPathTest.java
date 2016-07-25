package org.jboss.resteasy.test.resource.request;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.request.resource.DateFormatPathResource;
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
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests date encoding as query parameter
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DateFormatPathTest {
    static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DateFormatPathTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, DateFormatPathResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DateFormatPathTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test date 08/26/2009
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDate() throws Exception {
        Response response = client.target(generateURL("/widget/08%2F26%2F2009")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("08/26/2009", response.readEntity(String.class));
        response.close();
    }
}