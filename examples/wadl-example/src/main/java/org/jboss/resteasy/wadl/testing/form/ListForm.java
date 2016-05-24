package org.jboss.resteasy.wadl.testing.form;

import org.jboss.resteasy.annotations.Form;

import java.lang.String;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ListForm {

    @Form(prefix = "foos")
    private List<FooForm> fooForms;

    @Form(prefix = "maps")
    private Map<String, FooForm> maps;

    public List<FooForm> getFooForms() {
        return fooForms;
    }

    public void setFooForms(List<FooForm> fooForms) {
        this.fooForms = fooForms;
    }

    public Map<String, FooForm> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, FooForm> maps) {
        this.maps = maps;
    }
}
