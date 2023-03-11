package org.jboss.resteasy.test.providers.inputstream;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.inputstream.resource.InputStreamCloseInputStream;
import org.jboss.resteasy.test.providers.inputstream.resource.InputStreamCloseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-741
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputStreamCloseTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InputStreamCloseTest.class.getSimpleName());
        war.addClass(InputStreamCloseInputStream.class);
        return TestUtil.finishContainerPrepare(war, null, InputStreamCloseResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InputStreamCloseTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails New client test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void newClient() throws Exception {
        // Resource creates and returns InputStream.
        Response response = client.target(generateURL("/create/")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("hello", response.readEntity(String.class));
        response.close();

        // Verify previously created InputStream has been closed.
        response = client.target(generateURL("/test/")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }
}
