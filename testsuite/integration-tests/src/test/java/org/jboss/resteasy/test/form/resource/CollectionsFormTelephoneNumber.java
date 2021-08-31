package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

public class CollectionsFormTelephoneNumber {
   @FormParam("countryCode")
   public String countryCode;
   @FormParam("number")
   public String number;
}
