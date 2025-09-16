package org.jboss.resteasy.test.security.doseta;

import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RestBootstrap(SigningTest.TestApplication.class)
public class SigningTest {
    private static final Logger LOG = Logger.getLogger(SigningTest.class);
    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    public static PrivateKey badKey;
    @Inject
    public Client client;

    @Test
    public void testMe() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("dns/zones");
        Assertions.assertTrue(url.getFile().contains("zones"), "'zones' string not in " + url.getFile());
        Assertions.assertTrue(url.getFile().contains("dns"), "'dns' string not in " + url.getFile());
    }

    @BeforeAll
    public static void setup() throws Exception {

        repository = new DosetaKeyRepository();
        repository.setKeyStorePath("test.jks");
        repository.setKeyStorePassword("password");
        repository.setUseDns(false);
        repository.start();

        PrivateKey privateKey = repository.getKeyStore().getPrivateKey("test._domainKey.samplezone.org");
        if (privateKey == null)
            throw new Exception("Private Key is null!!!");
        PublicKey publicKey = repository.getKeyStore().getPublicKey("test._domainKey.samplezone.org");
        keys = new KeyPair(publicKey, privateKey);

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        badKey = keyPair.getPrivate();

        final ResteasyDeployment deployment = ResteasyContext.getContextData(ResteasyDeployment.class);
        deployment.getDispatcher().getDefaultContextObjects().put(KeyRepository.class, repository);
    }

    @Path("/signed")
    public interface SigningProxy {
        @GET
        @Verify
        @Produces("text/plain")
        @Path("bad-signature")
        String bad();

        @GET
        @Verify
        @Produces("text/plain")
        String hello();

        @POST
        @Consumes("text/plain")
        @Signed(selector = "test", domain = "samplezone.org")
        void postSimple(String input);
    }

    @Path("/signed")
    public static class SignedResource {
        @DELETE
        @Path("request-only")
        public Response deleteRequestOnly(@Context HttpHeaders headers,
                @Context UriInfo uriInfo,
                @HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature) {
            Assertions.assertNotNull(signature);
            //         System.out.println("Signature: " + signature);
            Verification verification = new Verification(keys.getPublic());
            verification.setBodyHashRequired(false);
            verification.getRequiredAttributes().put("method", "GET");
            verification.getRequiredAttributes().put("uri", uriInfo.getPath());
            try {
                verification.verify(signature, headers.getRequestHeaders(), null, keys.getPublic());
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
            String token = signature.getAttributes().get("token");
            signature = new DKIMSignature();
            signature.setDomain("samplezone.org");
            signature.setSelector("test");
            signature.setPrivateKey(keys.getPrivate());
            signature.setBodyHashRequired(false);
            signature.getAttributes().put("token", token);

            return Response.ok().header(DKIMSignature.DKIM_SIGNATURE, signature).build();

        }

        @GET
        @Produces("text/plain")
        @Path("bad-signature")
        public Response badSignature() throws Exception {
            DKIMSignature signature = new DKIMSignature();
            signature.setDomain("samplezone.org");
            signature.setSelector("test");
            signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());

            byte[] sig = { 0x0f, 0x03 };
            String encodedBadSig = Base64.getEncoder().encodeToString(sig);

            ParameterParser parser = new ParameterParser();
            String s = signature.toString();
            String header = parser.setAttribute(s.toCharArray(), 0, s.length(), ';', "b", encodedBadSig);

            signature.setSignature(sig);
            return Response.ok("hello world").header(DKIMSignature.DKIM_SIGNATURE, header).build();
        }

        @GET
        @Produces("text/plain")
        @Path("bad-hash")
        public Response badHash() throws Exception {
            DKIMSignature signature = new DKIMSignature();
            signature.setDomain("samplezone.org");
            signature.setSelector("test");
            signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());

            return Response.ok("hello").header(DKIMSignature.DKIM_SIGNATURE, signature.toString()).build();
        }

        @GET
        @Produces("text/plain")
        @Path("manual")
        public Response getManual() {
            DKIMSignature signature = new DKIMSignature();
            signature.setSelector("test");
            signature.setDomain("samplezone.org");
            Response.ResponseBuilder builder = Response.ok("hello");
            builder.header(DKIMSignature.DKIM_SIGNATURE, signature);
            return builder.build();
        }

        @GET
        @Path("header")
        @Produces("text/plain")
        public Response withHeader() {
            Response.ResponseBuilder builder = Response.ok("hello world");
            builder.header("custom", "value");
            DKIMSignature signature = new DKIMSignature();
            signature.setSelector("test");
            signature.setDomain("samplezone.org");
            signature.addHeader("custom");
            builder.header(DKIMSignature.DKIM_SIGNATURE, signature);
            return builder.build();
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org")
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
        }

        @POST
        @Consumes("text/plain")
        @Path("verify-manual")
        public void verifyManual(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature,
                @Context HttpHeaders headers, MarshalledEntity<String> input) throws Exception {
            Assertions.assertNotNull(signature);
            Assertions.assertEquals(input.getEntity(), "hello world");

            signature.verify(headers.getRequestHeaders(), input.getMarshalledBytes(), keys.getPublic());
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", timestamped = true)
        @Produces("text/plain")
        @Path("stamped")
        public String getStamp() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(seconds = 1))
        @Produces("text/plain")
        @Path("expires-short")
        public String getExpiresShort() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(minutes = 1))
        @Produces("text/plain")
        @Path("expires-minute")
        public String getExpiresMinute() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(hours = 1))
        @Produces("text/plain")
        @Path("expires-hour")
        public String getExpiresHour() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(days = 1))
        @Produces("text/plain")
        @Path("expires-day")
        public String getExpiresDay() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(months = 1))
        @Produces("text/plain")
        @Path("expires-month")
        public String getExpiresMonth() {
            return "hello world";
        }

        @GET
        @Signed(selector = "test", domain = "samplezone.org", expires = @After(years = 1))
        @Produces("text/plain")
        @Path("expires-year")
        public String getExpiresYear() {
            return "hello world";
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
    public void testRequestOnly() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/request-only"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setPrivateKey(keys.getPrivate());
        contentSignature.setBodyHashRequired(false);
        contentSignature.setAttribute("method", "GET");
        contentSignature.setAttribute("uri", "/signed/request-only");
        contentSignature.setAttribute("token", "1122");
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature).delete();

        Assertions.assertEquals(200, response.getStatus());
        String signatureHeader = (String) response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        contentSignature = new DKIMSignature(signatureHeader);
        Verification verification = new Verification(keys.getPublic());
        verification.setBodyHashRequired(false);
        verification.getRequiredAttributes().put("token", "1122");
        verification.verify(contentSignature, response.getStringHeaders(), null, keys.getPublic());
        response.close();

    }

    @Test
    public void testSigningManual() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        Response response = target.request().get();
        Assertions.assertEquals(200, response.getStatus());
        MarshalledEntity<String> marshalledEntity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });
        Assertions.assertEquals("hello world", marshalledEntity.getEntity());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        //      System.out.println(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        Assertions.assertNotNull(signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);
        contentSignature.verify(response.getStringHeaders(), marshalledEntity.getMarshalledBytes(), keys.getPublic());
        response.close();
    }

    @Test
    public void testBasicVerification() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setPrivateKey(keys.getPrivate());
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(204, response.getStatus());
        response.close();
    }

    @Test
    public void testManualVerification() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/verify-manual"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setDomain("samplezone.org");
        contentSignature.setSelector("test");
        contentSignature.setAttribute("code", "hello");
        contentSignature.setPrivateKey(keys.getPrivate());
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(204, response.getStatus());
        response.close();

    }

    @Test
    public void testBasicVerificationRepository() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        target.property(KeyRepository.class.getName(), repository);
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(204, response.getStatus());
        response.close();
    }

    @Test
    public void testBasicVerificationBadSignature() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        DKIMSignature contentSignature = new DKIMSignature();
        contentSignature.setSelector("test");
        contentSignature.setDomain("samplezone.org");
        contentSignature.setPrivateKey(badKey);
        Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature)
                .post(Entity.text("hello world"));
        Assertions.assertEquals(401, response.getStatus());
        response.close();
    }

    @Test
    public void testBasicVerificationNoSignature() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
        Response response = target.request().post(Entity.text("hello world"));
        Assertions.assertEquals(401, response.getStatus());
        response.close();
    }

    @Test
    public void testTimestampSignature() throws Exception {
        DKIMSignature signature = new DKIMSignature();
        signature.setTimestamp();
        signature.setSelector("test");
        signature.setDomain("samplezone.org");
        signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());
        String sig = signature.toString();
        //      System.out.println(DKIMSignature.DKIM_SIGNATURE + ": " + sig);
        signature = new DKIMSignature(sig);

    }

    @Test
    public void testTimestamp() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        verification.setStaleCheck(true);
        verification.setStaleSeconds(100);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/stamped"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        try {
            String output = response.readEntity(String.class);
        } catch (Exception e) {
            throw e;
        }
        response.close();

    }

    @Test
    public void testStaleTimestamp() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);
        verification.setStaleCheck(true);
        verification.setStaleSeconds(1);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/stamped"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        Thread.sleep(1500);
        try {
            String output = response.readEntity(String.class);
            Assertions.fail();
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            //         System.out.println("here");
            //         Assert.assertEquals("Failed to verify signatures:\r\n Signature is stale", e.getMessage());
            Assertions.assertTrue(e.getMessage().indexOf("Failed to verify signatures:\r\n") >= 0);
            Assertions.assertTrue(e.getMessage().indexOf("Signature is stale") >= 0);
        }
        response.close();

    }

    @Test
    public void testExpiresHour() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-hour"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        response.close();
    }

    @Test
    public void testExpiresMinutes() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-minute"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        response.close();
    }

    @Test
    public void testExpiresDays() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-day"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        response.close();
    }

    @Test
    public void testExpiresMonths() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-month"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        response.close();
    }

    @Test
    public void testExpiresYears() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-year"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        response.close();
    }

    @Test
    public void testExpiresFail() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/expires-short"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        Thread.sleep(1500);
        try {
            String output = response.readEntity(String.class);
            throw new Exception("unreachable!");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            //         Assert.assertEquals("Failed to verify signatures:\r\n Signature expired", e.getMessage());
            Assertions.assertTrue(e.getMessage().indexOf("Failed to verify signatures:\r\n") >= 0);
            Assertions.assertTrue(e.getMessage().indexOf("Signature expired") >= 0);
        }
        response.close();

    }

    @Test
    public void testManualFail() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.genKeyPair();

        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setKey(keyPair.getPublic());

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/manual"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        try {
            String output = response.readEntity(String.class);
            throw new Exception("unreachable!");
        } catch (ProcessingException pe) {
            UnauthorizedSignatureException e = (UnauthorizedSignatureException) pe.getCause();
            //         System.out.println("*************" + e.getMessage());
            //         Assert.assertEquals("Failed to verify signatures:\r\n Failed to verify signature.", e.getMessage());
            Assertions.assertTrue(e.getMessage().indexOf("Failed to verify signatures:\r\n") >= 0);
            Assertions.assertTrue(e.getMessage().indexOf("Failed to verify signature.") >= 0);
        }
        response.close();

    }

    @Test
    public void testManual() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/manual"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        Assertions.assertEquals("hello", output);
        response.close();
    }

    @Test
    public void testManualWithHeader() throws Exception {
        Verifier verifier = new Verifier();
        Verification verification = verifier.addNew();
        verification.setRepository(repository);

        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/header"));
        Invocation.Builder request = target.request();
        request.property(Verifier.class.getName(), verifier);
        Response response = request.get();
        //      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertNotNull(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
        Assertions.assertEquals(200, response.getStatus());
        String output = response.readEntity(String.class);
        Assertions.assertEquals("hello world", output);
        response.close();
    }

    @Test
    public void testBadSignature() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/bad-signature"));
        Response response = target.request().get();
        Assertions.assertEquals(200, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assertions.assertNotNull(signatureHeader);
        //      System.out.println(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });
        boolean failedVerification = false;

        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
        } catch (SignatureException e) {
            failedVerification = true;
        }
        Assertions.assertTrue(failedVerification);
        response.close();
    }

    @Test
    public void testBadHash() throws Exception {
        //ResteasyClientImpl client = new ResteasyClientImpl();
        WebTarget target = client.target(TestPortProvider.generateURL("/signed/bad-hash"));
        Response response = target.request().get();
        Assertions.assertEquals(200, response.getStatus());
        String signatureHeader = response.getHeaderString(DKIMSignature.DKIM_SIGNATURE);
        Assertions.assertNotNull(signatureHeader);
        //      System.out.println(DKIMSignature.DKIM_SIGNATURE + ":  " + signatureHeader);

        DKIMSignature contentSignature = new DKIMSignature(signatureHeader);

        MarshalledEntity<String> entity = response.readEntity(new GenericType<MarshalledEntity<String>>() {
        });

        boolean failedVerification = false;
        try {
            contentSignature.verify(response.getStringHeaders(), entity.getMarshalledBytes(), keys.getPublic());
        } catch (SignatureException e) {
            failedVerification = true;
        }
        Assertions.assertTrue(failedVerification);
        response.close();
    }

    @Test
    public void testProxy(final ResteasyWebTarget target) throws Exception {
        target.property(KeyRepository.class.getName(), repository);
        SigningProxy proxy = target.proxy(SigningProxy.class);
        String output = proxy.hello();
        proxy.postSimple("hello world");

    }

    @Test
    public void testBadSignatureProxy(final ResteasyWebTarget target) throws Exception {
        target.property(KeyRepository.class.getName(), repository);
        SigningProxy proxy = target.proxy(SigningProxy.class);
        try {
            String output = proxy.bad();
            throw new Exception("UNREACHABLE");
        } catch (ResponseProcessingException e) {
            LOG.error(e.getMessage(), e);
            //Assert.assertTrue(e.getCause() instanceof UnauthorizedSignatureException);
        }
    }

}
