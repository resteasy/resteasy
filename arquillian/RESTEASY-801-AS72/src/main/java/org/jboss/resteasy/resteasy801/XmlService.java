package org.jboss.resteasy.resteasy801;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;

@Path("/xml/products")
public class XmlService
{

   @GET
   @Produces("application/json")
   @Path("{id}")
   @BadgerFish
   public XmlProduct getProduct()
   {
      return new XmlProduct(333, "Iphone");
   }

   @GET
   @Produces("application/json")
   @NoJackson
   public XmlProduct[] getProducts()
   {

      XmlProduct[] products = {new XmlProduct(333, "Iphone"), new XmlProduct(44, "macbook")};
      return products;
   }

}
