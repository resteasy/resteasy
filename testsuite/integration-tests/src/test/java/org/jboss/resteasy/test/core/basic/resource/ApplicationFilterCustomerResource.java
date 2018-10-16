package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/customers")
public class ApplicationFilterCustomerResource {
   private Map<Integer, ApplicationFilterCustomer> customerDB = new ConcurrentHashMap<Integer, ApplicationFilterCustomer>();
   private AtomicInteger idCounter = new AtomicInteger();

   public ApplicationFilterCustomerResource() {
   }

   @POST
   @Consumes("application/xml")
   public Response createCustomer(ApplicationFilterCustomer customer) {
      customer.setId(idCounter.incrementAndGet());
      customerDB.put(customer.getId(), customer);
      return Response.created(URI.create("/customers/" + customer.getId())).build();

   }

   @GET
   @Path("{id}")
   @Produces("application/xml")
   public ApplicationFilterCustomer getCustomer(@PathParam("id") int id) {
      ApplicationFilterCustomer customer = customerDB.get(id);
      if (customer == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return customer;
   }

   @PUT
   @Path("{id}")
   @Consumes("application/xml")
   public void updateCustomer(@PathParam("id") int id, ApplicationFilterCustomer update) {
      ApplicationFilterCustomer current = customerDB.get(id);
      if (current == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }

      current.setFirstName(update.getFirstName());
      current.setLastName(update.getLastName());
      current.setStreet(update.getStreet());
      current.setState(update.getState());
      current.setZip(update.getZip());
      current.setCountry(update.getCountry());
   }
}
