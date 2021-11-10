package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/employeeinfo")
public class EmailResource
{
   @GET
   @Path("/employees/{firstname}.{lastname}@{domain}.com")
   @Produces("text/plain")
   public String getEmployeeLastName(@PathParam("lastname") String lastName)
   {
      return lastName;
   }
}
