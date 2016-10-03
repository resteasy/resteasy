package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("nameFilter")
public class JsonFilterParent {
    protected String name;
    protected int id;

    public JsonFilterParent() {

    }

    public JsonFilterParent(String name, int id) {
        this.name = name;
        this.id = id;
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
