package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.ClientBuilder;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClientEngine;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class TestLinks {
    private static NettyJaxrsServer server;
    private static Dispatcher dispatcher;

    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        ResteasyDeployment deployment = server.getDeployment();
        deployment.getActualProviderClasses().add(ObjectMapperProvider.class);
        deployment.start();
        dispatcher = deployment.getDispatcher();
        server.start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        server.stop();
        server = null;
        dispatcher = null;
    }

    private String url;
    private BookStoreService client;
    private CloseableHttpClient httpClient;

    @ParameterizedTest
    @ArgumentsSource(BookClassProvider.class)
    public void testLinks(Class resourceType, String type) throws Exception {
        POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), resourceType);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
        httpClient = HttpClientBuilder.create().build();
        ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(httpClient);
        url = generateBaseUrl();
        ResteasyWebTarget target = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
        client = target.proxy(BookStoreService.class);
        Book book = null;
        switch (type) {
            case "xml":
                book = client.getBookXML("foo");
                break;
            case "json":
                book = client.getBookJSON("foo");
                break;
        }
        checkBookLinks1(url, book);
        // TJWS does not support chunk encodings well so I need to kill kept
        // alive connections
        httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
        dispatcher.getRegistry().removeRegistrations(resourceType);
    }

    @ParameterizedTest
    @ArgumentsSource(BookClassProvider.class)
    public void testComments(Class resourceType, String type) throws Exception {
        POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), resourceType);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
        httpClient = HttpClientBuilder.create().build();
        ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(httpClient);
        url = generateBaseUrl();
        ResteasyWebTarget target = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
        client = target.proxy(BookStoreService.class);
        List<Comment> comments = null;
        switch (type) {
            case "xml":
                comments = client.getBookCommentsXML("foo");
                break;
            case "json":
                comments = client.getBookCommentsJSON("foo");
                break;
        }
        Assertions.assertNotNull(comments);
        Assertions.assertFalse(comments.isEmpty());
        checkCommentLinks(url, comments.get(0));
        // TJWS does not support chunk encodings well so I need to kill kept
        // alive connections
        httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
        dispatcher.getRegistry().removeRegistrations(resourceType);
    }

    private void checkBookLinks1(String url, Book book) {
        Assertions.assertNotNull(book);
        Assertions.assertEquals("foo", book.getTitle());
        Assertions.assertEquals("bar", book.getAuthor());
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(7, links.size());
        // self
        AtomLink atomLink = links.getLinkForRel("self");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo", atomLink.getHref());
        // update
        atomLink = links.getLinkForRel("update");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo", atomLink.getHref());
        // remove
        atomLink = links.getLinkForRel("remove");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo", atomLink.getHref());
        // list
        atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/books", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/books", atomLink.getHref());
        // comments
        atomLink = links.getLinkForRel("comments");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comments", atomLink.getHref());
        // comment collection
        atomLink = links.getLinkForRel("comment-collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment-collection", atomLink.getHref());
    }

    private void checkCommentLinks(String url, Comment comment) {
        Assertions.assertNotNull(comment);
        Assertions.assertEquals(Integer.toString(0), comment.getId());
        RESTServiceDiscovery links = comment.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(6, links.size());
        // self
        AtomLink atomLink = links.getLinkForRel("self");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment/0", atomLink.getHref());
        // update
        atomLink = links.getLinkForRel("update");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment/0", atomLink.getHref());
        // remove
        atomLink = links.getLinkForRel("remove");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment/0", atomLink.getHref());
        // list
        atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comments", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comments", atomLink.getHref());
        // collection
        atomLink = links.getLinkForRel("collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment-collection", atomLink.getHref());
    }
}
