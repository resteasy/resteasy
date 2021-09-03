package org.jboss.resteasy.test.providers.jackson2.resource;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;

@Path("/products")
public class Jackson2Resource {

   @GET
   @Produces("application/json")
   @Path("{id}")
   public Jackson2Product getProduct() {
      return new Jackson2Product(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   @Path("/formatted/{id}")
   @Formatted
   public Jackson2Product getFormattedProduct() {
      return new Jackson2Product(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   public Jackson2Product[] getProducts() {

      Jackson2Product[] products = {new Jackson2Product(333, "Iphone"), new Jackson2Product(44, "macbook")};
      return products;
   }

   @POST
   @Produces("application/foo+json")
   @Consumes("application/foo+json")
   @Path("{id}")
   public Jackson2Product post(Jackson2Product p) {
      return p;
   }

}
