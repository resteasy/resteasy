package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;

@Path("/products")
public class JacksonJaxbCoexistenceJacksonResource {

   @GET
   @Produces("application/json")
   @Path("{id}")
   public JacksonJaxbCoexistenceProduct2 getProduct() {
      return new JacksonJaxbCoexistenceProduct2(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   public JacksonJaxbCoexistenceProduct2[] getProducts() {

      JacksonJaxbCoexistenceProduct2[] products = {new JacksonJaxbCoexistenceProduct2(333, "Iphone"), new JacksonJaxbCoexistenceProduct2(44, "macbook")};
      return products;
   }

   @POST
   @Produces("application/foo+json")
   @Consumes("application/foo+json")
   @Path("{id}")
   public JacksonJaxbCoexistenceProduct2 post(JacksonJaxbCoexistenceProduct2 p) {
      return p;
   }

}
