package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;

@Path("/")
public class CollectionsFormResource {
    private static final String ERROR_MESSAGE = "Wrong form parameter";

    @Path("/person")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void post(@Form CollectionsFormPerson p) {
        assertEquals(ERROR_MESSAGE, 2, p.telephoneNumbers.size());
        assertEquals(ERROR_MESSAGE, 2, p.adresses.size());
        assertEquals(ERROR_MESSAGE, "31", p.telephoneNumbers.get(0).countryCode);
        assertEquals(ERROR_MESSAGE, "91", p.telephoneNumbers.get(1).countryCode);
        assertEquals(ERROR_MESSAGE, "Main Street", p.adresses.get("INVOICE").street);
        assertEquals(ERROR_MESSAGE, "Square One", p.adresses.get("SHIPPING").street);
    }
}
