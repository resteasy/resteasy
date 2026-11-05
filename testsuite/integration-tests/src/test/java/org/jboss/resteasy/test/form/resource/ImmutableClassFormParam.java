/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

/**
 * Immutable class for testing @BeanParam with constructor injection.
 * This demonstrates that constructor injection works for regular immutable classes,
 * not just Records (works on all Java versions).
 */
public class ImmutableClassFormParam {
    private final String username;
    private final String password;

    /**
     * Constructor with @FormParam annotations for constructor injection.
     */
    public ImmutableClassFormParam(
            @FormParam("username") String username,
            @FormParam("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
