package org.jboss.resteasy.jsapi.testing.form;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;

/**
 * 12 03 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class MyForm2 {
    @FormParam("stuff")
    private String stuff;

    @FormParam("number")
    private int number;

    @HeaderParam("myHeader")
    private String header;

    public String getStuff() {
        return stuff;
    }

    public void setStuff(String stuff) {
        this.stuff = stuff;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
