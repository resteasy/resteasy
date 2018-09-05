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
	Book getBookXML(@PathParam("id") String id);

	@Produces({"application/json"})
	@Path("book/{id}")
	@GET
	Book getBookJSON(@PathParam("id") String id);

	@Produces({"application/xml"})
	@Path("book/{id}/comments")
	@GET
	List<Comment> getBookCommentsXML(@PathParam("id") String id);

	@Produces({"application/json"})
	@Path("book/{id}/comments")
	@GET
	List<Comment> getBookCommentsJSON(@PathParam("id") String id);

	@Produces({"application/xml"})
	@GET
	@Path("book/{id}/comment-collection")
	ScrollableCollection getScrollableCommentsXML(@PathParam("id") String id, @MatrixParam("query") String query);

	@Produces({"application/json"})
	@GET
	@Path("book/{id}/comment-collection")
	ScrollableCollection getScrollableCommentsJSON(@PathParam("id") String id, @MatrixParam("query") String query);

}
