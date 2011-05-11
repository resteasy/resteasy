package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.security.doseta.DKIMSignature;
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
    * Sign a returned message using a private key named "test._domainKey.samplezone.org"
    * found in the key store.
    *
    * @return
    */
   @GET
   @Signed(selector = "anil", domain="server.com")
   @Produces("text/plain")
   public String hello()
   {
      return "hello world";
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
       System.out.println(signature);
       Assert.assertEquals(input, "hello world");
    }

}
