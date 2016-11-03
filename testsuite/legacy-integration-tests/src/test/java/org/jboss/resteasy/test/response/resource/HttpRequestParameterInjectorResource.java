package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
