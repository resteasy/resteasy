package org.jboss.resteasy.test.resource.param.resource;

public class HeaderParamMyClass {
    private String header = "badValue";

    public HeaderParamMyClass(){

    }

    public void setValue(String v) {
        header = v;
    }

    @Override
    public String toString() {
        return header;
    }
}
