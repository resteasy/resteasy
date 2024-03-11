package org.jboss.resteasy.test.form.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(2, p.telephoneNumbers.size(), errorMessage);
        assertEquals(2, p.adresses.size(), errorMessage);
        assertEquals("31", p.telephoneNumbers.get(0).country.code, errorMessage);
        assertEquals("91", p.telephoneNumbers.get(1).country.code, errorMessage);
        assertEquals("Main Street", p.adresses.get("INVOICE").street, errorMessage);
        assertEquals("NL", p.adresses.get("INVOICE").country.code, errorMessage);
        assertEquals("Square One", p.adresses.get("SHIPPING").street, errorMessage);
        assertEquals("IN", p.adresses.get("SHIPPING").country.code, errorMessage);
    }
}
