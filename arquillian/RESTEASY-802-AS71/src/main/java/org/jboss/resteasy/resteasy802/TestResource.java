package org.jboss.resteasy.resteasy802;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Local
public interface TestResource
{
	   @GET
	   @Path("test")
	   public Response test();
}