package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
