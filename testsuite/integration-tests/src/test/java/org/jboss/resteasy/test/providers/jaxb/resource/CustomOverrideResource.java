package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/test")
@Produces("application/xml")
public class CustomOverrideResource {
   @GET
   public Response getFooXml() {
      CustomOverrideFoo foo = new CustomOverrideFoo();
      foo.setName("bill");
      return Response.ok(foo).build();
   }

   @GET
   @Produces("text/x-vcard")
   public Response getFooVcard() {
      CustomOverrideFoo foo = new CustomOverrideFoo();
      foo.setName("bill");
      return Response.ok(foo).build();
   }
}
