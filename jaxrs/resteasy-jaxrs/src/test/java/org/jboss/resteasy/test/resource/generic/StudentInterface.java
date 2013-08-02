package org.jboss.resteasy.test.resource.generic;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This interface is used on the client side only.
 *
 * @author Jozef Hartinger
 */

@Path("/student/{id}")
@Produces("application/student")
@Consumes("application/student")
public interface StudentInterface
{
   @GET
   Student get(@PathParam("id") Integer id);

   @PUT
   void put(@PathParam("id") Integer id, Student entity);
}
