package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
        Assert.assertEquals("input", str.getEntity());
        Assert.assertTrue(str.verify(CryptoCertResource.cert));
    }
}
