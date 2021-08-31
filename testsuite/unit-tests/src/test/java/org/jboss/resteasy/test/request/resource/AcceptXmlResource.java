package org.jboss.resteasy.test.request.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/xml")
public class AcceptXmlResource {
   @Consumes("application/xml;schema=foo")
   @PUT
   public void putFoo(String foo) {
   }

   @Consumes("application/xml")
   @PUT
   public void put(String foo) {
   }

   @Consumes("application/xml;schema=bar")
   @PUT
   public void putBar(String foo) {
   }


}
