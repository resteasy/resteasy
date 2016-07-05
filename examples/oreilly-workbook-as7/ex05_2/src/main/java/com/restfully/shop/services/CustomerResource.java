package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/customers")
public class CustomerResource
{
   private Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
   private AtomicInteger idCounter = new AtomicInteger();

   public CustomerResource()
   {
   }

   @POST
   @Produces("text/html")
   public Response createCustomer(@FormParam("firstname") String first,
                                  @FormParam("lastname") String last)
   {
      Customer customer = new Customer();
      customer.setId(idCounter.incrementAndGet());
      customer.setFirstName(first);
      customer.setLastName(last);
      customerDB.put(customer.getId(), customer);
      System.out.println("Created customer " + customer.getId());
      String output = "Created customer <a href=\"customers/" + customer.getId() + "\">" + customer.getId() + "</a>";
      String lastVisit = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date());
      URI location = URI.create("/customers/" + customer.getId());
      return Response.created(location)
              .entity(output)
              .cookie(new NewCookie("last-visit", lastVisit))
              .build();

   }

   @GET
   @Path("{id}")
   @Produces("text/plain")
   public Response getCustomer(@PathParam("id") int id,
                               @HeaderParam("User-Agent") String userAgent,
                               @CookieParam("last-visit") String date)
   {
      final Customer customer = customerDB.get(id);
      if (customer == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      String output = "User-Agent: " + userAgent + "\r\n";
      output += "Last visit: " + date + "\r\n\r\n";
      output += "Customer: " + customer.getFirstName() + " " + customer.getLastName();
      String lastVisit = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date());
      return Response.ok(output)
              .cookie(new NewCookie("last-visit", lastVisit))
              .build();
   }

}
