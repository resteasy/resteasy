package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/signed")
public interface SigningProxy {
   @GET
   @Verify
   @Produces("text/plain")
   @Path("bad-signature")
   String bad();

   @GET
   @Verify
   @Produces("text/plain")
   String hello();

   @POST
   @Consumes("text/plain")
   @Signed(selector = "test", domain = "samplezone.org")
   void postSimple(String input);
}
