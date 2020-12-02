package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * Created by rsearls on 8/23/17.
 */
@Path("test")
public interface ProxyNullInputStreamResource {
   @HEAD
   @Path("/user/{db}")
   void getUserHead(@PathParam("db") String db);
}
