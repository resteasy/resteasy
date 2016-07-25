package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ComplexFormResource {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/person")
    public String post(@Form ComplexFormPerson p) {
        return p.toString();
    }
}
