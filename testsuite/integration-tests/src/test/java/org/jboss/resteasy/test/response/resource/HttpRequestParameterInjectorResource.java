package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/foo")
public class HttpRequestParameterInjectorResource {
   @GET
   @POST
   @Produces("text/plain")
   public String get(@HttpRequestParameterInjectorClassicParam("param") String param,
                      @QueryParam("param") @DefaultValue("") String query,
                      @FormParam("param") @DefaultValue("") String form) {
      return String.format("%s, %s, %s", param, query, form);
   }
}
