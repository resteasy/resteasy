package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
