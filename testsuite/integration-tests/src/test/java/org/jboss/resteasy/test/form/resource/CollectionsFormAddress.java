package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

public class CollectionsFormAddress {
   @FormParam("street")
   public String street;
   @FormParam("houseNumber")
   public String houseNumber;
}
