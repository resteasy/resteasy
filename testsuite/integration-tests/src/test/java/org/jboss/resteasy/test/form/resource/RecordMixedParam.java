/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;

/**
 * Java Record for testing @BeanParam with mixed parameter types.
 * Demonstrates that Records can use different JAX-RS parameter annotations.
 */
public record RecordMixedParam(
        @FormParam("name") String name,
        @FormParam("age") int age,
        @QueryParam("country") String country,
        @HeaderParam("X-User-Agent") String userAgent) {
}
