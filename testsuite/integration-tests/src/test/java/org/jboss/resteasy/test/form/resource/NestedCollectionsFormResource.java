package org.jboss.resteasy.test.form.resource;

import static org.junit.Assert.assertEquals;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;

@Path("/")
public class NestedCollectionsFormResource {
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/person")
    public void post(@Form NestedCollectionsFormPerson p) {
        String errorMessage = "Wrong received form";
        assertEquals(errorMessage, 2, p.telephoneNumbers.size());
        assertEquals(errorMessage, 2, p.adresses.size());
        assertEquals(errorMessage, "31", p.telephoneNumbers.get(0).country.code);
        assertEquals(errorMessage, "91", p.telephoneNumbers.get(1).country.code);
        assertEquals(errorMessage, "Main Street", p.adresses.get("INVOICE").street);
        assertEquals(errorMessage, "NL", p.adresses.get("INVOICE").country.code);
        assertEquals(errorMessage, "Square One", p.adresses.get("SHIPPING").street);
        assertEquals(errorMessage, "IN", p.adresses.get("SHIPPING").country.code);
    }
}
