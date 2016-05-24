package org.jboss.resteasy.jsapi.testing.sub;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public interface Book {
    @GET
    @Path("/title")
    @Produces("text/plain")
    String getTitle();

    @Path("/ch/{number}")
    Chapter getChapter(@PathParam("number") int number);
}
