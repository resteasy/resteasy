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
          @HeaderParam("Filter3") @DefaultValue("") String filter3,
          @HeaderParam("PreMatchFilter1") @DefaultValue("") String preMatchFilter1,
          @HeaderParam("PreMatchFilter2") @DefaultValue("") String preMatchFilter2,
          @HeaderParam("PreMatchFilter3") @DefaultValue("") String preMatchFilter3) {
       boolean async = filter1.contains("async")
             || filter2.contains("async")
             || filter3.contains("async")
             || preMatchFilter1.contains("async")
             || preMatchFilter2.contains("async")
             || preMatchFilter3.contains("async");
       if(async != request.getAsyncContext().isSuspended())
          return Response.serverError().entity("Request suspention is wrong").build();
       return Response.ok("resource").build();
    }
}
