package org.jboss.resteasy.test.crypto.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.jupiter.api.Assertions;

@Path("/smime/signed")
public class CryptoSignedResource {

    @GET
    @Produces("multipart/signed")
    public SignedOutput get() {
        SignedOutput output = new SignedOutput("hello world", "text/plain");
        output.setCertificate(CryptoCertResource.cert);
        output.setPrivateKey(CryptoCertResource.privateKey);
        return output;
    }

    @POST
    @Consumes("multipart/signed")
    public void post(SignedInput<String> input) throws Exception {
        String str = input.getEntity();
        Assertions.assertEquals("input", str);
        Assertions.assertTrue(input.verify(CryptoCertResource.cert));
    }
}
