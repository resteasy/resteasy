package org.jboss.resteasy.links.test;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

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

	@Produces({"application/xml"})
	@Path("book/{id}/comments")
	@GET
	public List<Comment> getBookCommentsXML(@PathParam("id") String id);

	@Produces({"application/json"})
	@Path("book/{id}/comments")
	@GET
	public List<Comment> getBookCommentsJSON(@PathParam("id") String id);

	@Produces({"application/xml"})
	@GET
	@Path("book/{id}/comment-collection")
	public ScrollableCollection getScrollableCommentsXML(@PathParam("id") String id, @MatrixParam("query") String query);

	@Produces({"application/json"})
	@GET
	@Path("book/{id}/comment-collection")
	public ScrollableCollection getScrollableCommentsJSON(@PathParam("id") String id, @MatrixParam("query") String query);

}
