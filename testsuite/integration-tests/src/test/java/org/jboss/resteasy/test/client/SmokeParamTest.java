package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.resource.SmokeParamResource;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke parameter test.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SmokeParamTest extends ClientTestBase{

    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SmokeParamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SmokeParamResource.class);
    }

    @AfterClass
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test one request with header and query parameter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSimple() throws Exception {
        Response response = client.target(generateURL("/foo")).request()
                .post(Entity.entity("hello world", "text/plain"));
        Assert.assertEquals("hello world", response.readEntity(String.class));
        response.close();

        response = client.target(generateURL("/foo")).queryParam("b", "world").request()
                .header("a", "hello")
                .get();
        Assert.assertEquals("hello world", response.readEntity(String.class));
        response.close();

    }
}