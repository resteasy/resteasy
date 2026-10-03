/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

/**
 * Java Record for testing @BeanParam with nullable values.
 */
public record RecordNullableParam(
        @FormParam("name") String name,
        @FormParam("email") String email) {
}
