package org.jboss.resteasy.jsapi.testing.form;

import javax.ws.rs.FormParam;

/**
 * 12 05 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class Address {
    @FormParam("street") private String street;
    @FormParam("houseNumber") private String houseNumber;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
