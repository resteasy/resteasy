package org.jboss.resteasy.jsapi.testing.sub;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("")
public interface Chapter {
    @GET
    @Path("title")
    @Produces("text/plain")
    String getTitle();

    @GET
    @Path("body")
    @Produces("text/plain")
    String getBody();
}
