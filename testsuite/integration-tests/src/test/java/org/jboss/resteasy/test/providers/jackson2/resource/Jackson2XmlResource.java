package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/xml/products")
public class Jackson2XmlResource {

   @GET
   @Produces("application/json")
   @Path("{id}")
   public Jackson2XmlProduct getProduct() {
      return new Jackson2XmlProduct(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   public Jackson2XmlProduct[] getProducts() {

      Jackson2XmlProduct[] products = {new Jackson2XmlProduct(333, "Iphone"), new Jackson2XmlProduct(44, "macbook")};
      return products;
   }

}
