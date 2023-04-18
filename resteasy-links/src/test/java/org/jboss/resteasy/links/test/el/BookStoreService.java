package org.jboss.resteasy.links.test.el;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.links.test.Book;

@Path("/")
public interface BookStoreService {

    @Produces({ "application/xml" })
    @Path("book/{id}")
    @GET
    Book getBookXML(@PathParam("id") String id);

    @Produces({ "application/json" })
    @Path("book/{id}")
    @GET
    Book getBookJSON(@PathParam("id") String id);
}
