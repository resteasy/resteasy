package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClassLinksProvider {

    private static NettyJaxrsServer server;

    private static Dispatcher dispatcher;

    private Client client;

    private String baseUrl;

    @BeforeClass
    public static void beforeClass() {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        ResteasyDeployment deployment = server.getDeployment();
        deployment.start();
        dispatcher = deployment.getDispatcher();
        server.start();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
        server = null;
        dispatcher = null;
    }

    @Before
    public void before() {
        POJOResourceFactory restServiceDiscoveryProviderServiceFactory = new POJOResourceFactory(new ResourceBuilder(),
                ClassLinksProviderService.class);
        POJOResourceFactory bookServiceFactory = new POJOResourceFactory(new ResourceBuilder(), BookStore.class);
        dispatcher.getRegistry().addResourceFactory(restServiceDiscoveryProviderServiceFactory);
        dispatcher.getRegistry().addResourceFactory(bookServiceFactory);

        client = ClientBuilder.newClient();
        baseUrl = generateBaseUrl();
    }

    @After
    public void after() {
        dispatcher.getRegistry().removeRegistrations(ClassLinksProviderService.class);
        client.close();
    }

    @Test
    public void shouldGetBookClassLinks() {
        RESTServiceDiscovery restServiceDiscovery = client.target(baseUrl)
                .queryParam("className", Book.class.getName())
                .request()
                .get(RESTServiceDiscovery.class);

        Assert.assertEquals(2, restServiceDiscovery.size());
        Assert.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUrl + "/books", "list")));
        Assert.assertTrue(restServiceDiscovery.contains(new RESTServiceDiscovery.AtomLink(baseUrl + "/books", "add")));
    }
}
