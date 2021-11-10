package org.jboss.resteasy.test.providers.atom.resource;

import org.jboss.resteasy.plugins.providers.atom.BaseLink;
import org.jboss.resteasy.plugins.providers.atom.RelativeLink;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/products")
public class LinkProductService {
   @GET
   @Produces("application/xml")
   @Path("{id}")
   public LinkProduct getProduct(@PathParam("id") int id) {
      LinkProduct p = new LinkProduct();
      p.setId(id);
      p.setName("iphone");
      p.getLinks().add(new RelativeLink("self", "/self"));
      p.getLinks().add(new BaseLink("create", "/products"));
      return p;
   }
}
