package org.jboss.resteasy.test.resource.basic.resource;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.test.resource.basic.ResponseCommittedTest;

@Path("")
public class ResponseCommittedResource {

   @GET
   @Path("")
   public Response works() throws Exception {

      Map<Class<?>, Object> contextDataMap = ResteasyContext.getContextDataMap();
      HttpResponse httpResponse = (HttpResponse) contextDataMap.get(HttpResponse.class);
      httpResponse.sendError(ResponseCommittedTest.TEST_STATUS);
      Response response = Response.ok("ok").build();
      return response;
   }
}
