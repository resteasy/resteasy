package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/smime/pkcs7-signature")
public class CryptoPkcs7SignedResource {
   @GET
   @Produces("application/pkcs7-signature")
   public SignedOutput get() {
      SignedOutput output = new SignedOutput("hello world", "text/plain");
      output.setCertificate(CryptoCertResource.cert);
      output.setPrivateKey(CryptoCertResource.privateKey);
      return output;
   }

   @GET
   @Path("text")
   @Produces("text/plain")
   public SignedOutput getText() {
      SignedOutput output = new SignedOutput("hello world", "text/plain");
      output.setCertificate(CryptoCertResource.cert);
      output.setPrivateKey(CryptoCertResource.privateKey);
      return output;
   }


   @POST
   @Consumes("application/pkcs7-signature")
   public void post(PKCS7SignatureInput<String> input) throws Exception {
      String str = input.getEntity(MediaType.TEXT_PLAIN_TYPE);
      Assert.assertEquals("input", str);
      Assert.assertTrue(input.verify(CryptoCertResource.cert));
   }
}
