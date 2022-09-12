package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("body")
public  interface FormBodyResourceClient {
   @PUT
   @Consumes("text/plain")
   @Produces("text/plain")
   String put(String value);
}
