package org.jboss.example.jaxrs2.simple;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/customers")
public class CustomerResource
{
   @GET
   @Produces("application/json")
   public Customer getByName(@QueryParam("name") String name)
   {
      return new Customer(name);
   }

   @GET
   @Path("{id}")
   @Produces("application/json")
   public Customer getById(@PathParam("id") String id)
   {
      return new Customer("Bill");
   }

}
