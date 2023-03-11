package org.jboss.resteasy.test;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.SniConfiguration;
import org.jboss.resteasy.test.util.SSLCerts;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests SNI capabilities for Netty based JaxRS server.
 *
 * @author Sebastian Łaskawiec
 * @see https://issues.jboss.org/browse/RESTEASY-1431
 */
public class SniTest {

    private static NettyJaxrsServer server;

    @BeforeClass
    public static void setup() {
        SniConfiguration sniConfiguration = new SniConfiguration(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext());
        sniConfiguration.addSniMapping("sni", SSLCerts.SNI_SERVER_KEYSTORE.getSslContext());
        sniConfiguration.addSniMapping("untrusted", SSLCerts.NO_TRUSTED_CLIENTS_KEYSTORE.getSslContext());

        server = new NettyJaxrsServer();
        server.setSniConfiguration(sniConfiguration);
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("");
        server.setSecurityDomain(null);
        server.getDeployment().getScannedResourceClasses().add(ResteasyTrailingSlashTest.Resource.class.getName());
        server.start();
    }

    @AfterClass
    public static void stop() throws Exception {
        server.stop();
    }

    @Test
    public void testTrustedClient() {
        //given
        Client client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());

        //when
        String returnValue = callRestService(client);

        //then
        Assert.assertNotNull(returnValue);
    }

    @Test(expected = ProcessingException.class)
    public void testTrustedClientButWithNoSNI() {
        //given
        Client client = createClientWithCertificate(SSLCerts.SNI_SERVER_KEYSTORE.getSslContext());

        //when
        callRestService(client);
    }

    @Test
    public void testSniClient() {
        //given
        ResteasyClient client = createClientWithCertificate(SSLCerts.SNI_TRUSTSTORE.getSslContext(), "sni");

        //when
        String returnValue = callRestService(client);

        //then
        Assert.assertNotNull(returnValue);
    }

    private ResteasyClient createClientWithCertificate(SSLContext sslContext, String... sniName) {
        ResteasyClientBuilder resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        if (sslContext != null) {
            resteasyClientBuilder.sslContext(sslContext);
        }
        if (sniName != null) {
            resteasyClientBuilder.sniHostNames(sniName);
        }
        return resteasyClientBuilder.build();
    }

    private String callRestService(Client client) {
        WebTarget target = client
                .target(String.format("https://%s:%d/test", TestPortProvider.getHost(), TestPortProvider.getPort()));
        return target.request().get(String.class);
    }

    @Path("/")
    public static class Resource {
        @GET
        @Path("/test/")
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "hello world";
        }
    }

}
