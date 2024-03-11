package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class CollectionsFormResource {
    private static final String ERROR_MESSAGE = "Wrong form parameter";

    @Path("/person")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void post(@Form CollectionsFormPerson p) {
        Assertions.assertEquals(2, p.telephoneNumbers.size(), ERROR_MESSAGE);
        Assertions.assertEquals(2, p.adresses.size(), ERROR_MESSAGE);
        Assertions.assertEquals("31", p.telephoneNumbers.get(0).countryCode, ERROR_MESSAGE);
        Assertions.assertEquals("91", p.telephoneNumbers.get(1).countryCode, ERROR_MESSAGE);
        Assertions.assertEquals("Main Street", p.adresses.get("INVOICE").street, ERROR_MESSAGE);
        Assertions.assertEquals("Square One", p.adresses.get("SHIPPING").street, ERROR_MESSAGE);
    }
}
