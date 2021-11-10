package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

public class ComplexFormAddress {
   @FormParam("street")
   public String street;
}
