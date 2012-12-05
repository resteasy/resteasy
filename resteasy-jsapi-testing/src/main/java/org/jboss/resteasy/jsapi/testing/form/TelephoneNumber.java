package org.jboss.resteasy.jsapi.testing.form;

import javax.ws.rs.FormParam;

/**
 * 12 05 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TelephoneNumber {
    @FormParam("countryCode") private String countryCode;
    @FormParam("number") private String number;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
