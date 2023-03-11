package org.jboss.resteasy.test.validation.resource;

@ValidationOnGetterNotNullOrOne
public class ValidationOnGetterStringBean {
    private String header;

    public String get() {
        return header;
    }

    public void set(String header) {
        this.header = header;
    }

    public ValidationOnGetterStringBean(final String header) {
        this.header = header;
    }
}
