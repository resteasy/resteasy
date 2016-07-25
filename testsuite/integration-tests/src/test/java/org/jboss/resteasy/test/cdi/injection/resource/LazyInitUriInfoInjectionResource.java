package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/test")
public class LazyInitUriInfoInjectionResource {
    private static Logger logger = Logger.getLogger(LazyInitUriInfoInjectionResource.class);

    private UriInfo info;

    @Context
    public void setUriInfo(UriInfo i) {
        this.info = i;
        logger.info(i.getClass().getName());
    }

    @GET
    @Produces("text/plain")
    public String get() {
        String val = info.getQueryParameters().getFirst("h");
        if (val == null) {
            val = "";
        }
        return val;
    }


}
