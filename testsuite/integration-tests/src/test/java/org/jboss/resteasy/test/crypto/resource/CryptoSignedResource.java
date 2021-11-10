package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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
