package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class EncodedPathResource
{
   @Path("/hello world")
   @GET
   public String get()
   {
      System.out.println("Hello");
      return "HELLO";
   }

   @Path("/goodbye%7Bworld")
   @GET
   public String goodbye()
   {
      System.out.println("Goodbye");
      return "GOODBYE";
   }
}
