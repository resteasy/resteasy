package org.jboss.resteasy.plugins.server.reactor.netty;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.netty.handler.ssl.ClientAuth;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.util.SSLCerts;
import org.jboss.resteasy.util.PortProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class BasicSSLTest extends BasicTest {

    private static Client client;

    @BeforeClass
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

    @AfterClass
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }
}
