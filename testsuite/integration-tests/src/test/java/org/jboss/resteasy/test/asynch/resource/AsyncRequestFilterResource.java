package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/")
public class AsyncRequestFilterResource {
   
    @GET
    public Response threeSyncRequestFilters(@Context HttpRequest request,
          @HeaderParam("Filter1") @DefaultValue("") String filter1,
          @HeaderParam("Filter2") @DefaultValue("") String filter2,
          @HeaderParam("Filter3") @DefaultValue("") String filter3) {
       boolean async = filter1.contains("async")
             || filter2.contains("async")
             || filter3.contains("async");
       System.err.println("Got request with filter1: "+filter1);
       System.err.println("Got request with filter2: "+filter2);
       System.err.println("Got request with filter3: "+filter3);
       if(async != request.getAsyncContext().isSuspended())
          return Response.serverError().entity("Request suspention is wrong").build();
       return Response.ok("resource").build();
    }
}
