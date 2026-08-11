/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

/**
 * Traditional mutable class with no-arg constructor for testing property injection.
 * This should use the PROPERTY injection path (not constructor injection).
 */
public class MutableClassFormParam {

    @FormParam("name")
    private String name;

    @FormParam("age")
    private int age;

    @FormParam("email")
    private String email;

    // No-arg constructor required for property injection
    public MutableClassFormParam() {
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    // Setters (required for property injection)
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
