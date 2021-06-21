package org.jboss.resteasy.test.bootstrap;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.Assert;
import org.junit.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.SeBootstrap.Configuration.SSLClientAuthentication;
import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class SeBootstrapTest {
    @Test
    public void testJAXRS() throws Exception {
        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().host("localhost").port(8080)
                .rootPath("bootstrap").build();
        CompletionStage<Instance> instance = SeBootstrap.start(new StandaloneApplication(), configuration);
        try {
            CompletionStage<Void> request = instance.thenAccept(ins -> {
                try (Client client = ClientBuilder.newClient()) {
                    Assert.assertEquals("BootStrapApi",
                            client.target("http://localhost:8080/bootstrap/produces/string").request().get(String.class));
                }
            });
            request.toCompletableFuture().get();
        } finally {
            if (instance.toCompletableFuture().isCompletedExceptionally()) {
                Assert.fail("Failed to start server with bootstrap api");
            } else {
                instance.toCompletableFuture().get().stop();
            }
        }
    }

    @Test
    public void testFailedStartJAXRS() throws Exception {
        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().host("localhost").port(8081)
                .rootPath("error").build();
        CompletionStage<Instance> instance = SeBootstrap.start(new ErrorApplication(), configuration);
        try {
            instance.toCompletableFuture().get();
        } catch (Exception e) {
            Assert.assertTrue("Server failed is expected", e.getMessage().indexOf("Could not find constructor") > -1);
        }
    }
    @Test
    public void testSSL() throws Exception {
        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().host("localhost").port(8443)
                .rootPath("ssl").sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
                .sslClientAuthentication(SSLClientAuthentication.NONE).build();
        CompletionStage<Instance> instance = SeBootstrap.start(new StandaloneApplication(), configuration);
        instance.toCompletableFuture().get();
        ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
        Assert.assertEquals("BootStrapApi",
                client.target("https://localhost:8443/ssl/produces/string").request().get(String.class));
    }

    @org.junit.Ignore
    //TODO:Fix this
    public void testSSLClientAuthRequired() throws Exception {
        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().host("localhost").port(8444)
                .rootPath("needclientauth").sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
                .sslClientAuthentication(SSLClientAuthentication.MANDATORY).build();
        CompletionStage<Instance> instance = SeBootstrap.start(new StandaloneApplication(), configuration);
        instance.toCompletableFuture().get();
        ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
        try {
            Assert.assertEquals("BootStrapApi",
                    client.target("https://localhost:8444/needclientauth/produces/string").request().get(String.class));
        } catch (Throwable e) {
            Assert.assertTrue(e.getCause() instanceof SSLHandshakeException);
        }
    }

    @org.junit.Ignore
    //TODO:Fix this
    public void testSSLClientAuthWant() throws Exception {
        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().host("localhost").port(8445)
                .rootPath("wantclientauth").sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
                .sslClientAuthentication(SSLClientAuthentication.OPTIONAL).build();
        CompletionStage<Instance> instance = SeBootstrap.start(new StandaloneApplication(), configuration);
        instance.toCompletableFuture().get();
        ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
        Assert.assertEquals("BootStrapApi",
                client.target("https://localhost:8445/wantclientauth/produces/string").request().get(String.class));
    }

    @Test
    public void testPropertyProvider() throws Exception {
        TestPropertyProvider propertyProvider = new TestPropertyProvider();
        SeBootstrap.Configuration config = SeBootstrap.Configuration.builder().from((name, type) -> {
            return propertyProvider.getValue(name, type);
        }).build();
        CompletionStage<Instance> instance = SeBootstrap.start(new StandaloneApplication(), config);
        instance.toCompletableFuture().get();
        try (Client client = ClientBuilder.newClient()) {
            Assert.assertEquals("BootStrapApi",
                    client.target("http://localhost:8999/propertyProvider/produces/string").request().get(String.class));
        }
    }

    private ResteasyClient createClientWithCertificate(SSLContext sslContext, String... sniName) {
        ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilderImpl();
        if (sslContext != null) {
            resteasyClientBuilder.sslContext(sslContext);
        }
        if (sniName != null) {
            resteasyClientBuilder.sniHostNames(sniName);
        }
        return resteasyClientBuilder.build();
    }

    class TestPropertyProvider {
        public Optional<Object> getValue(String name, Class<?> type) {
            if (name.equals(SeBootstrap.Configuration.HOST))
                return Optional.of("localhost");
            if (name.equals(SeBootstrap.Configuration.PORT)) {
                return Optional.of(8999);
            }
            if (name.equals(SeBootstrap.Configuration.ROOT_PATH)) {
                return Optional.of("propertyProvider");
            }
            return Optional.empty();
        }
    }

    public class StandaloneApplication extends jakarta.ws.rs.core.Application {
        public final Set<Object> singletons = new HashSet<Object>();

        public Set<Object> getSingletons() {
            singletons.add(new StringResource());
            return singletons;
        }
    }

    public class ErrorApplication extends jakarta.ws.rs.core.Application {
        // Error applicaton the StringResource requires a constructor
        public final Set<Class<?>> classes = new HashSet<Class<?>>();

        public Set<Class<?>> getClasses() {
            classes.add(StringResource.class);
            return classes;
        }
    }

    @Path("/")
    public class StringResource {
        @GET
        @Path("produces/string")
        @Produces("text/plain")
        public String produceString() {
            return "BootStrapApi";
        }
    }
}