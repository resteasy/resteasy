package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Link;

@Path("/")
public class LinkJaxbResource {
   @GET
   @Produces("application/xml")
   public LinkJaxbCustomer getCustomer() {
      LinkJaxbCustomer cust = new LinkJaxbCustomer("bill");
      Link link = Link.fromUri("a/b/c").build();
      cust.getLinks().add(link);
      link = Link.fromUri("c/d").rel("delete").build();
      cust.getLinks().add(link);
      return cust;
   }
}
