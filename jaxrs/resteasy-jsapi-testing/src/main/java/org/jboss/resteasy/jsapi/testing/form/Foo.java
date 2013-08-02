package org.jboss.resteasy.jsapi.testing.form;

import javax.ws.rs.FormParam;

/**
 * 12 03 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class Foo {
    @FormParam("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
}
