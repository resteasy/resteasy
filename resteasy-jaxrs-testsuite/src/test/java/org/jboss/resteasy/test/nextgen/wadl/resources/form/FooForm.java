package org.jboss.resteasy.test.nextgen.wadl.resources.form;

import javax.ws.rs.FormParam;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class FooForm {

    @FormParam("foo")
    private String foo;

    @FormParam("bar")
    private String bar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
}
