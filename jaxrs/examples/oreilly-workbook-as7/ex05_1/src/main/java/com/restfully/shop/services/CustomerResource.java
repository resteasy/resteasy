package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/customers")
public class CustomerResource
{
   private Map<Integer, Customer> customerDB = Collections.synchronizedMap(new LinkedHashMap<Integer, Customer>());

   public CustomerResource()
   {
      Customer customer;
      int id = 1;

      customer = new Customer();
      customer.setId(id);
      customer.setFirstName("Bill");
      customer.setLastName("Burke");
      customer.setStreet("263 Clarendon Street");
      customer.setCity("Boston");
      customer.setState("MA");
      customer.setZip("02115");
      customer.setCountry("USA");
      customerDB.put(id++, customer);

      customer = new Customer();
      customer.setId(id);
      customer.setFirstName("Joe");
      customer.setLastName("Burke");
      customer.setStreet("263 Clarendon Street");
      customer.setCity("Boston");
      customer.setState("MA");
      customer.setZip("02115");
      customer.setCountry("USA");
      customerDB.put(id++, customer);

      customer = new Customer();
      customer.setId(id);
      customer.setFirstName("Monica");
      customer.setLastName("Burke");
      customer.setStreet("263 Clarendon Street");
      customer.setCity("Boston");
      customer.setState("MA");
      customer.setZip("02115");
      customer.setCountry("USA");
      customerDB.put(id++, customer);

      customer = new Customer();
      customer.setId(id);
      customer.setFirstName("Steve");
      customer.setLastName("Burke");
      customer.setStreet("263 Clarendon Street");
      customer.setCity("Boston");
      customer.setState("MA");
      customer.setZip("02115");
      customer.setCountry("USA");
      customerDB.put(id++, customer);
   }

   @GET
   @Produces("application/xml")
   public StreamingOutput getCustomers(final @QueryParam("start") int start,
                                       final @QueryParam("size") @DefaultValue("2") int size)
   {
      return new StreamingOutput()
      {
         public void write(OutputStream outputStream) throws IOException, WebApplicationException
         {
            PrintStream writer = new PrintStream(outputStream);
            writer.println("<customers>");
            synchronized (customerDB)
            {
               int i = 0;
               for (Customer customer : customerDB.values())
               {
                  if (i >= start && i < start + size) outputCustomer("   ", writer, customer);
                  i++;
               }
            }
            writer.println("</customers>");
         }
      };
   }

   @GET
   @Produces("application/xml")
   @Path("uriinfo")
   public StreamingOutput getCustomers(@Context UriInfo info)
   {
      int start = 0;
      int size = 2;
      if (info.getQueryParameters().containsKey("start"))
      {
         start = Integer.valueOf(info.getQueryParameters().getFirst("start"));
      }
      if (info.getQueryParameters().containsKey("size"))
      {
         size = Integer.valueOf(info.getQueryParameters().getFirst("size"));
      }
      return getCustomers(start, size);
   }

   protected void outputCustomer(String indent, PrintStream writer, Customer cust) throws IOException
   {
      writer.println(indent + "<customer id=\"" + cust.getId() + "\">");
      writer.println(indent + "   <first-name>" + cust.getFirstName() + "</first-name>");
      writer.println(indent + "   <last-name>" + cust.getLastName() + "</last-name>");
      writer.println(indent + "   <street>" + cust.getStreet() + "</street>");
      writer.println(indent + "   <city>" + cust.getCity() + "</city>");
      writer.println(indent + "   <state>" + cust.getState() + "</state>");
      writer.println(indent + "   <zip>" + cust.getZip() + "</zip>");
      writer.println(indent + "   <country>" + cust.getCountry() + "</country>");
      writer.println(indent + "</customer>");
   }

}
