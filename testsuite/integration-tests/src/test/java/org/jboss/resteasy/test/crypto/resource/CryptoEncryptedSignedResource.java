package org.jboss.resteasy.test.crypto.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.jupiter.api.Assertions;

@Path("/smime/encrypted/signed")
public class CryptoEncryptedSignedResource {
    @GET
    public EnvelopedOutput get() {
        SignedOutput signed = new SignedOutput("hello world", "text/plain");
        signed.setCertificate(CryptoCertResource.cert);
        signed.setPrivateKey(CryptoCertResource.privateKey);

        EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
        output.setCertificate(CryptoCertResource.cert);
        return output;
    }

    @POST
    public void post(EnvelopedInput<SignedInput<String>> input) throws Exception {
        SignedInput<String> str = input.getEntity(CryptoCertResource.privateKey, CryptoCertResource.cert);
        Assertions.assertEquals("input", str.getEntity());
        Assertions.assertTrue(str.verify(CryptoCertResource.cert));
    }
}
