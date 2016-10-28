package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
        Assert.assertEquals("input", str);
        Assert.assertTrue(input.verify(CryptoCertResource.cert));
    }
}
