package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/jxml/products")
public class JacksonJaxbCoexistenceJacksonXmlResource {

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

}
