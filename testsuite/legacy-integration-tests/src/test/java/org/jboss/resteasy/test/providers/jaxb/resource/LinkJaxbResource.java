package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;

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
