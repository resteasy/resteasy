package org.jboss.resteasy.tests.typevar.sample;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


public interface Hello<T> {

	   @POST
	   @Path("/hello")
	   @Produces("text/plain")
	   @Consumes("text/plain")
	   String sayHi(T in);
}
