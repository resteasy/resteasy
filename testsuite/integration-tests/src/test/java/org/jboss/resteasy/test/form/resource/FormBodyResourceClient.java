package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("body")
public  interface FormBodyResourceClient {
    @PUT
    @Consumes("text/plain")
    @Produces("text/plain")
    String put(String value);
}
