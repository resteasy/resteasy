package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/")
public interface SuperStringConverterMyClient {
   @Path("person/{person}")
   @PUT
   void put(@PathParam("person") SuperStringConverterPerson p);

   @Path("company/{company}")
   @PUT
   void putCompany(@PathParam("company") SuperStringConverterCompany c);
}
