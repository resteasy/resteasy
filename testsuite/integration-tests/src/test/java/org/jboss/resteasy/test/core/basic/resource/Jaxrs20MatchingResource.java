package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("")
public class Jaxrs20MatchingResource {

   @GET
   @Path("path/match")
   public String get()
   {
      return "get";
   }

   @POST
   @Path("path/{param}")
   public String post(@PathParam("param") String param, String entity)
   {
      return param;
   }
}
