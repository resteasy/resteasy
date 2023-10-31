package org.jboss.resteasy.links.test.el;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.links.test.BookStoreService;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLinksInvalidEL {

    private static NettyJaxrsServer server;
    private static Dispatcher dispatcher;
    private static final Logger LOG = Logger.getLogger(TestLinksInvalidEL.class);

    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        ResteasyDeployment deployment = server.getDeployment();
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

    private Class<?> resourceType;
    private String url;
    private BookStoreService client;
    private HttpClient httpClient;

    public TestLinksInvalidEL() {
        this.resourceType = BookStoreInvalidEL.class;
    }

    @BeforeEach
    public void before() {
        POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), resourceType);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
        httpClient = HttpClientBuilder.create().build();
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient);
        url = generateBaseUrl();
        ResteasyWebTarget target = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
        client = target.proxy(BookStoreService.class);
    }

    @SuppressWarnings("deprecation")
    @AfterEach
    public void after() {
        // TJWS does not support chunk encodings well so I need to kill kept
        // alive connections
        httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
        dispatcher.getRegistry().removeRegistrations(resourceType);
    }

    @Test
    public void testELWorksWithoutPackageXML() throws Exception {
        try {
            client.getBookXML("foo");
            Assertions.fail("This should have caused a 500");
        } catch (InternalServerErrorException x) {
            LOG.error("Failure is " + x.getResponse().readEntity(String.class));
            Assertions.assertEquals(500, x.getResponse().getStatus());
        } catch (Exception x) {
            Assertions.fail("Expected InternalServerErrorException");
        }
    }

    @Test
    public void testELWorksWithoutPackageJSON() throws Exception {
        try {
            client.getBookJSON("foo");
            Assertions.fail("This should have caused a 500");
        } catch (InternalServerErrorException x) {
            LOG.error("Failure is " + x.getResponse().readEntity(String.class));
            Assertions.assertEquals(500, x.getResponse().getStatus());
        } catch (Exception x) {
            Assertions.fail("Expected InternalServerErrorException");
        }
    }
}
