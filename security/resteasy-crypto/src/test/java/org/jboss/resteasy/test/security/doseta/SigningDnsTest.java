package org.jboss.resteasy.test.security.doseta;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import se.unlogic.eagledns.EagleDNS;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RestBootstrap(SigningDnsTest.TestApplication.class)
public class SigningDnsTest {
    public static DosetaKeyRepository clientRepository;
    public static DosetaKeyRepository serverRepository;
    public static PrivateKey badKey;

    @Inject
    private Client client;

    @BeforeAll
    public static void setup() throws Exception {

        clientRepository = new DosetaKeyRepository();
        clientRepository.setKeyStorePath("test1.jks");
        clientRepository.setKeyStorePassword("password");
        clientRepository.setUseDns(true);
        clientRepository.setDnsUri("dns://localhost:6363");
        clientRepository.start();

        serverRepository = new DosetaKeyRepository();
        serverRepository.setKeyStorePath("test2.jks");
        serverRepository.setKeyStorePassword("password");
        serverRepository.setUseDns(true);
        serverRepository.setDnsUri("dns://localhost:6363");
        serverRepository.start();

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        badKey = keyPair.getPrivate();

        final ResteasyDeployment deployment = ResteasyContext.getContextData(ResteasyDeployment.class);
        deployment.getDispatcher().getDefaultContextObjects().put(KeyRepository.class, serverRepository);
        configureDNS();
    }

    private static EagleDNS dns;

    public static void configureDNS() throws Exception {
        dns = new EagleDNS();
        dns.setConfigClassPath("dns/conf/config.xml");
        dns.start();
    }

    @AfterAll
    public static void shutdownDns() {
        dns.shutdown();
    }

    @Path("/signed")
    public static class SignedResource {
        @GET
        @Produces("text/plain")
        @Path("bad-signature")
        public Response badSignature() throws Exception {
            DKIMSignature signature = new DKIMSignature();
            signature.setDomain("samplezone.org");
            signature.setSelector("test2");
            signature.setPrivateKey(badKey);

            return Response.ok("hello world").header(DKIMSignature.DKIM_SIGNATURE, signature).build();
        }

        @GET
        @Signed(selector = "test2", domain = "samplezone.org")
        @Produces("text/plain")
        public String hello() {
            return "hello world";
        }

        @POST
        @Consumes("text/plain")
        @Verify
        public void post(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature, String input) {
            Assertions.assertNotNull(signature);
            Assertions.assertEquals(input, "hello world");
            //         System.out.println(signature);
        }

    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(SignedResource.class);
        }
    }

    @Test
    public void testBasicVerificationRepository() throws Exception {
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test1");
        contentSignature.setDomain("samplezone.org");
        target.property(KeyRepository.class.getName(), clientRepository);
        Builder request = target.request();
        request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
        Response response = request.post(Entity.entity("hello world", "text/plain"));
        Assertions.assertEquals(204, response.getStatus());
        response.close();

    }

    @Test
    public void testBasicVerificationBadSignature() throws Exception {
        Builder request = client.target(TestPortProvider.generateURL("/signed")).request();
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test1");
        contentSignature.setDomain("samplezone.org");
        contentSignature.setPrivateKey(badKey);
        request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
        Response response = request.post(Entity.entity("hello world", "text/plain"));
        Assertions.assertEquals(401, response.getStatus());
        response.close();
    }

    @Test
    public void testBasicVerificationNoSignature() throws Exception {
        Builder request = client.target(TestPortProvider.generateURL("/signed")).request();
        Response response = request.post(Entity.entity("hello world", "text/plain"));
        Assertions.assertEquals(401, response.getStatus());
        response.close();
    }

}
