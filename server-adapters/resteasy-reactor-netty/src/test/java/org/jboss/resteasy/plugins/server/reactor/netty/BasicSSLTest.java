package org.jboss.resteasy.plugins.server.reactor.netty;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.util.SSLCerts;
import org.jboss.resteasy.util.PortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import io.netty.handler.ssl.ClientAuth;

public class BasicSSLTest extends BasicTest {

    private static Client client;

    @BeforeAll
    public static void setup() {
        final SSLContext clientContext = SSLCerts.DEFAULT_TRUSTSTORE.getSslContext();
        final SSLContext serverContext = SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext();

        final ReactorNettyJaxrsServer reactorNettyJaxrsServer = new ReactorNettyJaxrsServer();
        reactorNettyJaxrsServer.setPort(PortProvider.getPort());
        reactorNettyJaxrsServer.setRootResourcePath("");
        reactorNettyJaxrsServer.setSecurityDomain(null);
        reactorNettyJaxrsServer.setSSLContext(serverContext);
        reactorNettyJaxrsServer.setClientAuth(ClientAuth.OPTIONAL);

        final ResteasyDeployment deployment = ReactorNettyContainer.start(reactorNettyJaxrsServer);
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);

        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);

        client = ClientBuilder
                .newBuilder()
                .sslContext(clientContext)
                .build()
                .register(JacksonJsonProvider.class);

        BasicTest.setupClient(client);
        BasicTest.setupBaseUrl("https://%s:%d%s");
    }

    @AfterAll
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }
}
