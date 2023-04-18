package org.jboss.resteasy.test.core.servlet.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public interface ServletMappingProxy {
    @POST
    @Path("formtestit")
    String postForm(@FormParam("value") String value);
}
