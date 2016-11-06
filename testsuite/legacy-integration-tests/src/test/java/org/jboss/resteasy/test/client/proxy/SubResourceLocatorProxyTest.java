package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.SubResourceLocatorProxyBookResource;
import org.jboss.resteasy.test.client.proxy.resource.SubResourceLocatorProxyChapterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SubResourceLocatorProxyTest {

    public interface Book {
        @GET
        @Path("/title")
        @Produces("text/plain")
        String getTitle();

        @Path("/ch/{number}")
        Chapter getChapter(@PathParam("number") int number);
    }

    public interface Chapter {
        @GET
        @Path("title")
        @Produces("text/plain")
        String getTitle();

        @GET
        @Path("body")
        @Produces("text/plain")
        String getBody();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SubResourceLocatorProxyTest.class.getSimpleName());
        war.addClass(SubResourceLocatorProxyTest.class);
        return TestUtil.finishContainerPrepare(war, null, SubResourceLocatorProxyBookResource.class,
                SubResourceLocatorProxyChapterResource.class);
    }

    static ResteasyClient client;

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SubResourceLocatorProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends request thru client proxy. The processing of the response goes first to the Book
     * resource which creates Chapter subresource and creates the response.
     * @tpPassCrit Expected string is returned in the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresourceProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/gulliverstravels"));
        Book book = target.proxy(Book.class);

        Assert.assertEquals("GET request thru client proxy failed", "Gulliver's Travels", book.getTitle());

        Chapter ch1 = book.getChapter(1);
        Assert.assertEquals("GET request thru client proxy failed", "Chapter 1", ch1.getTitle());

        Chapter ch2 = book.getChapter(2);
        Assert.assertEquals("GET request thru client proxy failed", "Chapter 2", ch2.getTitle());
    }
}
