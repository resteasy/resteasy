package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.HeaderParam;

public class FormResourceClientFormSecond {
    @HeaderParam("custom-header")
    protected String foo;


    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}
