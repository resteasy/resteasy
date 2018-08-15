package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by rsearls on 8/23/17.
 */
@Path("test")
public interface ProxyNullInputStreamResource {
   @HEAD
   @Path("/user/{db}")
   void getUserHead(@PathParam("db") String db);
}

