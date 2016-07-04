package org.jboss.resteasy.test.nextgen.wadl.resources.form;

import org.jboss.resteasy.annotations.Form;

import java.util.List;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ListForm {

    @Form(prefix = "foos")
    private List<FooForm> fooForms;

    public List<FooForm> getFooForms() {
        return fooForms;
    }

    public void setFooForms(List<FooForm> fooForms) {
        this.fooForms = fooForms;
    }
}
