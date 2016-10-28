package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Old client test)
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Old client test)
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.LinkHeaderDelegate;
import org.jboss.resteasy.client.LinkHeader;
import org.jboss.resteasy.test.client.resource.LinkHeaderService;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails LinkHeader (it use deprecated Link). Test also new Link provided by jax-rs 2.0 spec
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LinkHeaderTest extends ClientTestBase{

    protected static ResteasyClient client;

    @Before
    public void setup() throws Exception {
        client = new ResteasyClientBuilder().build();
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
     * @tpTestDetails Test new client without API and old client with API
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLinkheader() throws Exception {
        // old client testing
        {
            ClientRequest request = new ClientRequest(generateURL("/linkheader/str"));
            request.addLink("previous chapter", "previous", "http://example.com/TheBook/chapter2", null);
            ClientResponse response = request.post();
            LinkHeader header = response.getLinkHeader();
            Assert.assertNotNull(header);
            Assert.assertTrue("Wrong link", header.getLinksByTitle().containsKey("previous chapter"));
            Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("previous"));
            Assert.assertEquals("Wrong link", header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");
        }

        // new client testing
        {
            Response response = client.target(generateURL("/linkheader/str")).request().header("Link",
                    "<http://example.com/TheBook/chapter2>; rel=\"previous\"; title=\"previous chapter\"").post(Entity.text(new String()));
            javax.ws.rs.core.Link link = response.getLink("previous");
            Assert.assertNotNull(link);
            Assert.assertEquals("Wrong link", "previous chapter", link.getTitle());
            Assert.assertEquals("Wrong link", "http://example.com/TheBook/chapter2", link.getUri().toString());
        }
    }

    /**
     * @tpTestDetails Test old client without API
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLinkheaderOldClientWithoutApi() throws Exception {
        LinkHeaderDelegate delegate = new LinkHeaderDelegate();
        LinkHeader header = delegate.fromString("<http://example.org/>; rel=index;\n" +
                "             rel=\"start http://example.net/relation/other\"");
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("index"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("start"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
        String str = delegate.toString(header);
        header = delegate.fromString(str);
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("index"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("start"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
        ClientRequest request = new ClientRequest(generateURL("/linkheader"));
        request.header("link", "<http://example.org/>; rel=index;" +
                "             rel=\"start http://example.net/relation/other\"");
        ClientResponse response = request.post();
        header = response.getLinkHeader();
        Assert.assertNotNull("Wrong link", header);
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("index"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("start"));
        Assert.assertTrue("Wrong link", header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
    }

}
