package org.jboss.resteasy.test.providers.jackson2.resource;

public class JacksonJaxbCoexistenceProduct {
    protected String name;

    protected int id;

    public JacksonJaxbCoexistenceProduct() {
    }

    public JacksonJaxbCoexistenceProduct(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
