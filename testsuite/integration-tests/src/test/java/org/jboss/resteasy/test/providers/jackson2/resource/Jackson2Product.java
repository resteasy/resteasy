package org.jboss.resteasy.test.providers.jackson2.resource;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class Jackson2Product {
   
    protected String name;
    protected int id;
    public Jackson2Product() {
    }
    @JsonPropertyOrder({ "name", "id" })
    public Jackson2Product(final int id, final String name) {
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
