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
       boolean async = isAsync(filter1)
             || isAsync(filter2)
             || isAsync(filter3)
             || isAsync(preMatchFilter1)
             || isAsync(preMatchFilter2)
             || isAsync(preMatchFilter3);
       if(async != request.getAsyncContext().isSuspended())
          return Response.serverError().entity("Request suspention is wrong").build();
       return Response.ok("resource").build();
    }

   private boolean isAsync(String filter)
   {
      return filter.equals("async-pass")
            || filter.equals("async-fail");
   }
}
