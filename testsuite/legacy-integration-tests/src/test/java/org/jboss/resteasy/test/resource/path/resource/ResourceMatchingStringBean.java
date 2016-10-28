package org.jboss.resteasy.test.resource.path.resource;

public class ResourceMatchingStringBean {
    private String header;

    public String get() {
        return header;
    }

    public void set(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "CallbackStringBean. To get a value, use rather #get() method.";
    }

    public ResourceMatchingStringBean(final String header) {
        super();
        this.header = header;
    }
}
