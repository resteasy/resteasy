package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/xml/products")
public class JacksonJaxbCoexistenceXmlResource {

   @GET
   @Produces("application/json")
   @Path("{id}")
   public JacksonJaxbCoexistenceXmlProduct getProduct() {
      return new JacksonJaxbCoexistenceXmlProduct(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   public JacksonJaxbCoexistenceXmlProduct[] getProducts() {

      JacksonJaxbCoexistenceXmlProduct[] products = {new JacksonJaxbCoexistenceXmlProduct(333, "Iphone"), new JacksonJaxbCoexistenceXmlProduct(44, "macbook")};
      return products;
   }

}
