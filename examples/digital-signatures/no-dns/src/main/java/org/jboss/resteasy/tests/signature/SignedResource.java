package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/signed")
public class SignedResource
{
   /**
    * Sign a returned message using a private key named "test._domainKey.samplezone.org"
    * found in the key store.
    *
    * @return
    */
   @GET
   @Signed(selector = "test", domain = "samplezone.org")
   @Produces("text/plain")
   public String hello()
   {
      return "hello world";
   }

   /**
    * Sign a returned message using a private key named "test._domainKey.samplezone.org"
    * found in the key store.  A header named "custom" is included in the signature calculation.
    * The signature header is created manually here instead of using annotations.
    *
    * @return
    */
   @GET
   @Path("manual-header")
   @Produces("text/plain")
   public Response withHeader()
   {
      Response.ResponseBuilder builder = Response.ok("hello world");
      builder.header("custom", "value");
      DKIMSignature signature = new DKIMSignature();
      signature.setSelector("test");
      signature.setDomain("samplezone.org");
      signature.addHeader("custom");
      builder.header(DKIMSignature.DKIM_SIGNATURE, signature);
      return builder.build();
   }

   /**
    * Verify a posted signature. Inject it and print it out too.
    *
    * @param signature
    * @param input
    */
   @POST
   @Consumes("text/plain")
   @Verify
   public void post(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature, String input)
   {
      Assert.assertNotNull(signature);
      Assert.assertEquals(input, "hello world");
   }

   /**
    * Sign a returned message, but also add a timestamp to the signature calculation.
    *
    * @return
    */
   @GET
   @Signed(selector = "test", domain = "samplezone.org",
           timestamped = true)
   @Produces("text/plain")
   @Path("stamped")
   public String getStamp()
   {
      return "hello world";
   }

   /**
    * Sign a message setting the expiration field of the DKIM-Signature to be 1 second after the current time
    *
    * @return
    */
   @GET
   @Signed(selector = "test", domain = "samplezone.org",
           expires = @After(seconds = 1))
   @Produces("text/plain")
   @Path("expires-short")
   public String getExpiresShort()
   {
      return "hello world";
   }

   /**
    * Sign a message setting the expiration field of the DKIM-Signature to be 1 minute after the current time
    *
    * @return
    */
   @GET
   @Signed(selector = "test", domain = "samplezone.org",
           expires = @After(minutes = 1))
   @Produces("text/plain")
   @Path("expires-minute")
   public String getExpiresMinute()
   {
      return "hello world";
   }

   /**
    * Example of sending a signature signed by a bad or expired key.
    *
    * @return
    * @throws Exception
    */
   @GET
   @Produces("text/plain")
   @Path("bad-signature")
   public Response badSignature() throws Exception
   {
      DKIMSignature signature = new DKIMSignature();
      signature.setDomain("samplezone.org");
      signature.setSelector("test");
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      PrivateKey badKey = keyPair.getPrivate();
      signature.setPrivateKey(badKey);

      return Response.ok("hello world").header(DKIMSignature.DKIM_SIGNATURE, signature).build();
   }


}
