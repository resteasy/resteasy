package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.Customers;
import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/customers")
public interface CustomerResource
{
   @POST
   @Consumes("application/xml")
   Response createCustomer(Customer customer, @Context UriInfo uriInfo);

   @GET
   @Produces("application/xml")
   @Formatted
   Customers getCustomers(@QueryParam("start") int start,
                          @QueryParam("size") @DefaultValue("2") int size,
                          @QueryParam("firstName") String firstName,
                          @QueryParam("lastName") String lastName,
                          @Context UriInfo uriInfo);

   @GET
   @Path("{id}")
   @Produces({"application/xml", "application/json"})
   Customer getCustomer(@PathParam("id") int id);
}
