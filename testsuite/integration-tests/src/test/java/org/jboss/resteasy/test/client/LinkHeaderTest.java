package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.resource.LinkHeaderService;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for new Link provided by jax-rs 2.0 spec
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LinkHeaderTest extends ClientTestBase {

    protected static ResteasyClient client;

    @Before
    public void setup() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(LinkHeaderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, LinkHeaderService.class);
    }

    @After
    public void shutdown() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test new client without API
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLinkheader() throws Exception {

        // new client testing
        {
            Response response = client.target(generateURL("/linkheader/str")).request().header("Link",
                    "<http://example.com/TheBook/chapter2>; rel=\"previous\"; title=\"previous chapter\"")
                    .post(Entity.text(new String()));
            jakarta.ws.rs.core.Link link = response.getLink("previous");
            Assert.assertNotNull(link);
            Assert.assertEquals("Wrong link", "previous chapter", link.getTitle());
            Assert.assertEquals("Wrong link", "http://example.com/TheBook/chapter2", link.getUri().toString());
        }
    }
}
