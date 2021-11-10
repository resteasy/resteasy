package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;;

@Path("resource")
public class JaxbElementResource {

   @POST
   @Path("standardwriter")
   public String bytearraywriter(String value) {
      return value;
   }
}
