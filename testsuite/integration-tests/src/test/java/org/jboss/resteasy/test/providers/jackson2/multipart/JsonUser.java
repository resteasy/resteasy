package org.jboss.resteasy.test.providers.jackson2.multipart;

public class JsonUser {
    private String name;

    public JsonUser() {
    }

    public JsonUser(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}