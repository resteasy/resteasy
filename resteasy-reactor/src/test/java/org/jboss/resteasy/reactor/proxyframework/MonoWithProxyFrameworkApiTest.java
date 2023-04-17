package org.jboss.resteasy.reactor.proxyframework;

import static org.jboss.resteasy.reactor.proxyframework.CustomResource.THE_CUSTOM_RESOURCE;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ReactorNettyClientHttpEngine;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

public class MonoWithProxyFrameworkApiTest {

    private static int port;

    @Path("api/v1")
    public interface RemoteCustomResource {

        @GET
        @Path("resource/custom")
        Mono<String> getCustomResource();
    }

    private static NettyJaxrsServer server;

    @BeforeClass
    public static void beforeClass() {
        port = TestPortProvider.getPort();
        server = new NettyJaxrsServer();
        server.setPort(port);
        server.setRootResourcePath("/");
        Arrays.asList((Class<?>[]) new Class[] { CustomResource.class })
                .forEach(server.getDeployment().getActualResourceClasses()::add);
        server.getDeployment().start();
        server.getDeployment().registration();
        server.start();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
        server = null;
    }

    private ResteasyClient client;

    @Before
    public void before() {
        final ReactorNettyClientHttpEngine reactorEngine = new ReactorNettyClientHttpEngine(
                HttpClient.create(),
                new DefaultChannelGroup(new DefaultEventExecutor()),
                ConnectionProvider.newConnection());
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .httpEngine(reactorEngine)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void givenRemoteServiceInterfaceAndWorkingRemoteServiceWhenProxyThenGenerateProxyWithMono() {
        final var proxy = client.target("http://localhost:" + port).proxy(RemoteCustomResource.class);
        assertEquals(THE_CUSTOM_RESOURCE, proxy.getCustomResource().block());

    }
}
