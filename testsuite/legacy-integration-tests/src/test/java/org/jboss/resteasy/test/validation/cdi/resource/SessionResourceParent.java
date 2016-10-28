package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface SessionResourceParent {
    @GET
    @Path("resource")
    String test(@Size(min = 4) @QueryParam("param") String param);
}