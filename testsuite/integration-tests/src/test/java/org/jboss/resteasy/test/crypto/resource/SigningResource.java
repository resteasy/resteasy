package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;

@Path("/signed")
public class SigningResource {

    public static KeyPair keys;
    public static DosetaKeyRepository repository;
    public static PrivateKey badKey;

    static {
        repository = new DosetaKeyRepository();
        repository.setKeyStorePath("test.jks");
        repository.setKeyStorePassword("password");
        repository.setUseDns(false);
        repository.start();

        PrivateKey privateKey = repository.getKeyStore().getPrivateKey("test._domainKey.samplezone.org");
        if (privateKey == null) {
            throw new RuntimeException("Private Key is null!!!");
        }
        PublicKey publicKey = repository.getKeyStore().getPublicKey("test._domainKey.samplezone.org");
        keys = new KeyPair(publicKey, privateKey);

        try {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            badKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate new RSA key pair", e);
        }
    }

    @DELETE
    @Path("request-only")
    public Response deleteRequestOnly(@Context HttpHeaders headers,
                                      @Context UriInfo uriInfo,
                                      @HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature) {
        Assert.assertNotNull(signature);
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
        signature.sign(new HashMap<>(), "hello world".getBytes(), keys.getPrivate());

        byte[] sig = {0x0f, 0x03};
        String encodedBadSig = Base64.encodeBytes(sig);

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
        signature.sign(new HashMap<>(), "hello world".getBytes(), keys.getPrivate());

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
        Assert.assertNotNull(signature);
        Assert.assertEquals(input, "hello world");
    }

    @POST
    @Consumes("text/plain")
    @Path("verify-manual")
    public void verifyManual(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature, @Context HttpHeaders headers, MarshalledEntity<String> input) throws Exception {
        Assert.assertNotNull(signature);
        Assert.assertEquals(input.getEntity(), "hello world");

        signature.verify(headers.getRequestHeaders(), input.getMarshalledBytes(), keys.getPublic());
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            timestamped = true)
    @Produces("text/plain")
    @Path("stamped")
    public String getStamp() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(seconds = 1))
    @Produces("text/plain")
    @Path("expires-short")
    public String getExpiresShort() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(minutes = 1))
    @Produces("text/plain")
    @Path("expires-minute")
    public String getExpiresMinute() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(hours = 1))
    @Produces("text/plain")
    @Path("expires-hour")
    public String getExpiresHour() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(days = 1))
    @Produces("text/plain")
    @Path("expires-day")
    public String getExpiresDay() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(months = 1))
    @Produces("text/plain")
    @Path("expires-month")
    public String getExpiresMonth() {
        return "hello world";
    }

    @GET
    @Signed(selector = "test", domain = "samplezone.org",
            expires = @After(years = 1))
    @Produces("text/plain")
    @Path("expires-year")
    public String getExpiresYear() {
        return "hello world";
    }

    @GET
    @Path("nobody")
    @Consumes("text/plain")
    @Verify(bodyHashRequired = false)
    public String get(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature) {
        Assert.assertNotNull(signature);
        return "xyz";
    }
}
