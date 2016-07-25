package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class QueryParamWithMultipleEqualsResource {
    @Path("test")
    @GET
    public String test(@QueryParam("foo") String incoming) {
        return incoming;
    }
}
