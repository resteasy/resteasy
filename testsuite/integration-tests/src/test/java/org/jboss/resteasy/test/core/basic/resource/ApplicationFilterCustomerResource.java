package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
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
