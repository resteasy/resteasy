package org.jboss.resteasy.test.resource.request.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

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
