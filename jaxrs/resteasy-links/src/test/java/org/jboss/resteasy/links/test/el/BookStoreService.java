package org.jboss.resteasy.links.test.el;

import org.jboss.resteasy.links.test.Book;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public interface BookStoreService {
	
	@Produces({"application/xml"})
	@Path("book/{id}")
	@GET
	public Book getBookXML(@PathParam("id") String id);

	@Produces({"application/json"})
	@Path("book/{id}")
	@GET
	public Book getBookJSON(@PathParam("id") String id);
}
