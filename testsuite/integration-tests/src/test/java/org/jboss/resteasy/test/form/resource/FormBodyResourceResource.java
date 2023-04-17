package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;

@Path("body")
public class FormBodyResourceResource {
    @PUT
    @Consumes("text/plain")
    @Produces("text/plain")
    public String put(@Form FormBodyResourceForm form) {
        return form.body + ".gotIt";
    }
}
