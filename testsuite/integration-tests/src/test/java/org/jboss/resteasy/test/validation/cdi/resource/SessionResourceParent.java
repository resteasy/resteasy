package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

public interface SessionResourceParent {
   @GET
   @Path("resource")
   String test(@Size(min = 4) @QueryParam("param") String param);
}
