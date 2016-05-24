package org.jboss.resteasy.jsapi.testing.form;

import org.jboss.resteasy.annotations.Form;

import java.util.List;

/**
 * 12 04 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class MyForm3 {
    @Form(prefix="foos")
    List<Foo> foos;

    public List<Foo> getFoos() {
        return foos;
    }

    public void setFoos(List<Foo> foos) {
        this.foos = foos;
    }
}
