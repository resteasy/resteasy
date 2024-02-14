package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.SubResourceLocatorProxyBookResource;
import org.jboss.resteasy.test.client.proxy.resource.SubResourceLocatorProxyChapterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SubResourceLocatorProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends request thru client proxy. The processing of the response goes first to the Book
     *                resource which creates Chapter subresource and creates the response.
     * @tpPassCrit Expected string is returned in the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresourceProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/gulliverstravels"));
        Book book = target.proxy(Book.class);

        Assertions.assertEquals("Gulliver's Travels", book.getTitle(),
                "GET request thru client proxy failed");

        Chapter ch1 = book.getChapter(1);
        Assertions.assertEquals("Chapter 1", ch1.getTitle(),
                "GET request thru client proxy failed");

        Chapter ch2 = book.getChapter(2);
        Assertions.assertEquals("Chapter 2", ch2.getTitle(),
                "GET request thru client proxy failed");
    }
}
