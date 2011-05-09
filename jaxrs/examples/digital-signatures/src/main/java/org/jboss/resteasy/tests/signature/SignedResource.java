package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.security.PublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/signed")
public class SignedResource
{
   /**
    * Sign the message by creating the ContentSignature header through the api.
    *
    * @return
    */
   @GET
   @Produces("text/plain")
   @Path("manual")
   public Response getManual()
   {
      ContentSignature signature = new ContentSignature();
      signature.setKeyAlias("test");
      Response.ResponseBuilder builder = Response.ok("hello");
      builder.header("Content-Signature", signature);
      return builder.build();
   }

   /**
    * Simple example signing message with KeyStore key alias "test"
    *
    * @return
    */
   @GET
   @Signed(keyAlias = "test")
   @Produces("text/plain")
   public String hello()
   {
      return "hello world";
   }

   /**
    * Simple example signing message with KeyStore key alias "test" found from signer() attribute
    *
    * @return
    */
   @GET
   @Signed(signer = "test")
   @Produces("text/plain")
   @Path("with-signer")
   public String withSigner()
   {
      return "hello world";
   }

   /**
    * Sets a timestamp attribute
    *
    * @return
    */
   @GET
   @Signed(signer = "test", timestamped = true)
   @Produces("text/plain")
   @Path("stamped")
   public String getStamp()
   {
      return "hello world";
   }

   /**
    * Sets the expires attribute on the signature with a very short expiration time
    *
    * @return
    */
   @GET
   @Signed(signer = "test", expires = @After(seconds = 1))
   @Produces("text/plain")
   @Path("expires-short")
   public String getExpiresShort()
   {
      return "hello world";
   }

   @GET
   @Signed(signer = "test", expires = @After(minutes = 1))
   @Produces("text/plain")
   @Path("expires-minute")
   public String getExpiresMinute()
   {
      return "hello world";
   }

   /**
    * Server-side verification test.  No key alias information is in the Content-Signature header
    * so use the keyAlias.
    *
    * The signature parameter is not required, it is included so that we can view the transmited header
    *
    * @param input
    */
   @POST
   @Consumes("text/plain")
   @Verify(keyAlias = "test")
   public void postSimple(@HeaderParam("Content-Signature") ContentSignature signature, String input)
   {
      System.out.println("Received Content-Signature: " + signature);
      Assert.assertEquals(input, "hello world");
   }

   /**
    * Client will be signing and passing a signer attribute
    *
    * @param signatures
    * @param input
    */
   @POST
   @Consumes("text/plain")
   @Path("by-signer")
   @Verify
   public void postBySigner(@HeaderParam("Content-Signature") ContentSignature signature, String input)
   {
      Assert.assertNotNull(signature);
      System.out.println("Received Content-Signature: " + signature);
      Assert.assertEquals(input, "hello world");
   }

   @POST
   @Consumes("text/plain")
   @Path("verify-manual")
   public void verifyManual(@HeaderParam("Content-Signature") ContentSignature signature,
                            @Context HttpHeaders headers,
                            @Context KeyRepository repository,
                            MarshalledEntity<String> input) throws Exception
   {
      Assert.assertNotNull(signature);
      System.out.println("Received Content-Signature: " + signature);
      Assert.assertEquals(input.getEntity(), "hello world");

      PublicKey publicKey = repository.getPublicKey("test");
      Assert.assertTrue(signature.verify(headers.getRequestHeaders(), input.getMarshalledBytes(), publicKey));
   }




}
