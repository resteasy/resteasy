package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("params")
public class OptionParamsResource {
   @Path("/customers/{custid}/phonenumbers")
   @GET
   @Produces("text/plain")
   public String getPhoneNumbers() {
      return "912-111-1111";
   }

   @Path("/customers/{custid}/phonenumbers/{id}")
   @GET
   @Produces("text/plain")
   public String getPhoneIds() {
      return "1111";
   }
}
