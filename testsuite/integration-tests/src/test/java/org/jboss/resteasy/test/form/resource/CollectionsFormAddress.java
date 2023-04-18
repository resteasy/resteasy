package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.FormParam;

public class CollectionsFormAddress {
    @FormParam("street")
    public String street;
    @FormParam("houseNumber")
    public String houseNumber;
}
