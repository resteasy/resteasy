package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public interface ServletMappingProxy {
   @POST
   @Path("formtestit")
   String postForm(@FormParam("value") String value);
}
