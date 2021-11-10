package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/curly")
public class ReaderWriterCurlyBraces {
   @Path("{tableName:[a-z][a-z0-9_]{0,49}}")
   @GET
   @Produces("text/plain")
   public String get(@PathParam("tableName") String param) {
      return "param";
   }
}
