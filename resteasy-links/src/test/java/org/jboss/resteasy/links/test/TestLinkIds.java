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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLinkIds {
    private static NettyJaxrsServer server;

    private static Dispatcher dispatcher;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        ResteasyDeployment deployment = server.getDeployment();
        deployment.start();
        dispatcher = deployment.getDispatcher();
        POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), IDServiceTestBean.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
        server.start();

        httpClient = HttpClientBuilder.create().build();
        ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(httpClient);
        url = generateBaseUrl();
        ResteasyWebTarget target = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
        client = target.proxy(IDServiceTest.class);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
        server = null;
        dispatcher = null;
    }

    private static String url;
    private static IDServiceTest client;
    private static CloseableHttpClient httpClient;

    @SuppressWarnings("deprecation")
    @After
    public void after() {
        // TJWS does not support chunk encodings well so I need to kill kept
        // alive connections
        httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testResourceId() throws Exception {
        IdBook book = client.getResourceIdBook("foo");
        checkBook(book, "/resource-id/book/foo");
    }

    @Test
    public void testResourceIds() throws Exception {
        IdBook book = client.getResourceIdsBook("foo", "bar");
        checkBook(book, "/resource-ids/book/foo/bar");
    }

    @Test
    public void testResourceIdMethod() throws Exception {
        IdBook book = client.getResourceIdMethodBook("foo");
        checkBook(book, "/resource-id-method/book/foo");
    }

    @Test
    public void testResourceIdsMethod() throws Exception {
        IdBook book = client.getResourceIdsMethodBook("foo", "bar");
        checkBook(book, "/resource-ids-method/book/foo/bar");
    }

    @Test
    public void testXmlId() throws Exception {
        IdBook book = client.getXmlIdBook("foo");
        checkBook(book, "/xml-id/book/foo");
    }

    @Test
    public void testJpaId() throws Exception {
        IdBook book = client.getJpaIdBook("foo");
        checkBook(book, "/jpa-id/book/foo");
    }

    private void checkBook(IdBook book, String relativeUrl) {
        Assert.assertNotNull(book);
        RESTServiceDiscovery links = book.getRest();
        Assert.assertNotNull(links);
        Assert.assertEquals(1, links.size());
        AtomLink link = links.get(0);
        Assert.assertEquals("self", link.getRel());
        Assert.assertEquals(url + relativeUrl, link.getHref());
    }
}
