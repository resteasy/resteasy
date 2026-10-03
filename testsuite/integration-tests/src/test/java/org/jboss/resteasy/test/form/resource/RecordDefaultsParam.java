/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;

/**
 * Java Record for testing @BeanParam with default values.
 */
public record RecordDefaultsParam(
        @FormParam("name") String name,
        @FormParam("age") @DefaultValue("18") int age) {
}
