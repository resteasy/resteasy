package org.jboss.resteasy.links.test.el;

import org.jboss.resteasy.links.test.Book;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/")
public interface BookStoreService {

   @Produces({"application/xml"})
   @Path("book/{id}")
   @GET
   Book getBookXML(@PathParam("id") String id);

   @Produces({"application/json"})
   @Path("book/{id}")
   @GET
   Book getBookJSON(@PathParam("id") String id);
}
