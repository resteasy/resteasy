package org.jboss.resteasy.plugins.server.reactor.netty;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

public class BasicTest extends AbstractBasicTest {
    private static Client client;

    public BasicTest() {
        super("http");
    }

    @BeforeAll
    public static void setup() throws Exception {
        final ResteasyDeployment deployment = ReactorNettyContainer.start();
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    @Override
    protected Client client() {
        return client;
    }
}
