package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

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

public class TestFacadeLinks {
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
        ScrollableCollection comments = null;
        switch (type) {
            case "xml":
                comments = client.getScrollableCommentsXML("foo", "book");
                break;
            case "json":
                comments = client.getScrollableCommentsJSON("foo", "book");
                break;

        }
        checkCommentsLinks(url, comments);
        // TJWS does not support chunk encodings well so I need to kill kept
        // alive connections
        httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
        dispatcher.getRegistry().removeRegistrations(resourceType);
    }

    private void checkCommentsLinks(String url, ScrollableCollection comments) {
        Assertions.assertNotNull(comments);
        RESTServiceDiscovery links = comments.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(5, links.size());
        // list
        AtomLink atomLink = links.getLinkForRel("list");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comments", atomLink.getHref());
        // add
        atomLink = links.getLinkForRel("add");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comments", atomLink.getHref());
        // comment collection
        atomLink = links.getLinkForRel("collection");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment-collection", atomLink.getHref());
        // next
        atomLink = links.getLinkForRel("next");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/book/foo/comment-collection;query=book?start=1&limit=1", atomLink.getHref());
        // home
        atomLink = links.getLinkForRel("home");
        Assertions.assertNotNull(atomLink);
        Assertions.assertEquals(url + "/", atomLink.getHref());
    }
}
