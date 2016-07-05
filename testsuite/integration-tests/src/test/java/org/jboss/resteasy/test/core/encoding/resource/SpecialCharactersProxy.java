package org.jboss.resteasy.test.core.encoding.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path(value = "/sayhello")
public interface SpecialCharactersProxy {

    @GET
    @Path("/en/{in}")
    @Produces("text/plain")
    String sayHi(@PathParam(value = "in") String in);

    @POST
    @Path("/compile")
    String compile(@QueryParam("query") String queryText);


}
