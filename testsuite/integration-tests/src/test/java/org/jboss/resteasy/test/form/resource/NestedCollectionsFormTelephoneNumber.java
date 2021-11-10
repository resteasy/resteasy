package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import jakarta.ws.rs.FormParam;

public class NestedCollectionsFormTelephoneNumber {
   @Form(prefix = "country")
   public NestedCollectionsFormCountry country;
   @FormParam("number")
   public String number;
}
