package org.jboss.resteasy.test.resource.request.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/request")
public class RequestResource
{
   @GET
   @Produces("text/plain")
   public String getRequest(@Context HttpRequest req)
   {
      return req.getRemoteAddress() + "/" + req.getRemoteHost();
   }
}
