package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.junit.Assert;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/smime/encrypted")
public class CryptoEncryptedResource {
   @GET
   public EnvelopedOutput get() {
      EnvelopedOutput output = new EnvelopedOutput("hello world", "text/plain");
      output.setCertificate(CryptoCertResource.cert);
      return output;
   }

   @POST
   public void post(EnvelopedInput<String> input) {
      String str = input.getEntity(CryptoCertResource.privateKey, CryptoCertResource.cert);
      Assert.assertEquals("input", str);
   }
}
