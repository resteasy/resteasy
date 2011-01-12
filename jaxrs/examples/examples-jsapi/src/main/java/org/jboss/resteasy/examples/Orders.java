package org.jboss.resteasy.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("orders")
public class Orders {

	@Path("{id}")
	@GET
	@Produces("text/plain")
	public String getOrder(@PathParam("id") String id) {
		return "Order Id: " + id;
	}

}
