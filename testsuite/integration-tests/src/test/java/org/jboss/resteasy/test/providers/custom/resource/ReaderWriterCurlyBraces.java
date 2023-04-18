package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/curly")
public class ReaderWriterCurlyBraces {
    @Path("{tableName:[a-z][a-z0-9_]{0,49}}")
    @GET
    @Produces("text/plain")
    public String get(@PathParam("tableName") String param) {
        return "param";
    }
}
