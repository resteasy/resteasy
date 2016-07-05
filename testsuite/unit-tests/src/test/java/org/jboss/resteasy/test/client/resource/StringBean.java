package org.jboss.resteasy.test.client.resource;

public class StringBean {
    private String header;

    public String get() {
        return header;
    }

    public void set(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "StringBean. To get a value, use rather #get() method.";
    }

    public StringBean(final String header) {
        super();
        this.header = header;
    }
}
