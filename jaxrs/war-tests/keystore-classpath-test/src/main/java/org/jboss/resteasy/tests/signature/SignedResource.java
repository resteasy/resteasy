package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.signature.After;
import org.jboss.resteasy.annotations.security.signature.Signed;
import org.jboss.resteasy.annotations.security.signature.Verify;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/signed")
public class SignedResource
{
   @GET
   @Signed(keyAlias = "test")
   @Produces("text/plain")
   public String hello()
   {
      return "hello world";
   }

   @GET
   @Signed(signer = "test")
   @Produces("text/plain")
   @Path("with-signer")
   public String withSigner()
   {
      return "hello world";
   }

   @POST
   @Consumes("text/plain")
   @Verify(keyAlias = "test")
   public void post(String input)
   {
      Assert.assertEquals(input, "hello world");
   }

   @GET
   @Signed(signer = "test", timestamped = true)
   @Produces("text/plain")
   @Path("stamped")
   public String getStamp()
   {
      return "hello world";
   }

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

   @GET
   @Signed(signer = "test", expires = @After(hours = 1))
   @Produces("text/plain")
   @Path("expires-hour")
   public String getExpiresHour()
   {
      return "hello world";
   }

   @GET
   @Signed(signer = "test", expires = @After(days = 1))
   @Produces("text/plain")
   @Path("expires-day")
   public String getExpiresDay()
   {
      return "hello world";
   }

   @GET
   @Signed(signer = "test", expires = @After(months = 1))
   @Produces("text/plain")
   @Path("expires-month")
   public String getExpiresMonth()
   {
      return "hello world";
   }

   @GET
   @Signed(signer = "test", expires = @After(years = 1))
   @Produces("text/plain")
   @Path("expires-year")
   public String getExpiresYear()
   {
      return "hello world";
   }
}
