package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.FormParam;

public class NestedCollectionsFormAddress {
    @FormParam("street")
    public String street;
    @FormParam("houseNumber")
    public String houseNumber;
    @Form(prefix = "country")
    public NestedCollectionsFormCountry country;
}
