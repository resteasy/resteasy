package org.jboss.resteasy.test.request.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/xml")
public class AcceptXmlResourceSecond {
   @Consumes("application/xml;schema=foo")
   @Produces("application/xml;schema=junk")
   @PUT
   public String putFoo(String foo) {
      return "hello";
   }

   @Consumes("application/xml;schema=bar")
   @Produces("application/xml;schema=stuff")
   @PUT
   public String putBar(String foo) {
      return "hello";
   }

   @Consumes("application/xml")
   @Produces("application/xml;schema=stuff")
   @PUT
   public String put(String foo) {
      return "hello";
   }

}
