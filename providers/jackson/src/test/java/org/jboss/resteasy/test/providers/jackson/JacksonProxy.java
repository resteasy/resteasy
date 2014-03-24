package org.jboss.resteasy.test.providers.jackson;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/products")
   public interface JacksonProxy
   {
      @GET
      @Produces("application/json")
      @Path("{id}")
      Product getProduct();

      @GET
      @Produces("application/json")
      Product[] getProducts();

      @POST
      @Produces("application/foo+json")
      @Consumes("application/foo+json")
      @Path("{id}")
      Product post(@PathParam("id") int id, Product p);
   }