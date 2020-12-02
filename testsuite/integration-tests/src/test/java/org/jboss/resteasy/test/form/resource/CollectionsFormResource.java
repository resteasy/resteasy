package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;
import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class CollectionsFormResource {
   private static final String ERROR_MESSAGE = "Wrong form parameter";

   @Path("/person")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   public void post(@Form CollectionsFormPerson p) {
      Assert.assertEquals(ERROR_MESSAGE, 2, p.telephoneNumbers.size());
      Assert.assertEquals(ERROR_MESSAGE, 2, p.adresses.size());
      Assert.assertEquals(ERROR_MESSAGE, "31", p.telephoneNumbers.get(0).countryCode);
      Assert.assertEquals(ERROR_MESSAGE, "91", p.telephoneNumbers.get(1).countryCode);
      Assert.assertEquals(ERROR_MESSAGE, "Main Street", p.adresses.get("INVOICE").street);
      Assert.assertEquals(ERROR_MESSAGE, "Square One", p.adresses.get("SHIPPING").street);
   }
}
