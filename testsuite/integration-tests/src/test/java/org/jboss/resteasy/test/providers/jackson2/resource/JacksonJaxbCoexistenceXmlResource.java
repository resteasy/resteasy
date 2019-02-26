package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
