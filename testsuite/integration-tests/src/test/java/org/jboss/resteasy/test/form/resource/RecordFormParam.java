package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

/**
 * Java Record for testing @BeanParam with @FormParam annotations.
 * Records are immutable data carriers introduced in Java 16.
 */
public record RecordFormParam(
        @FormParam("name") String name,
        @FormParam("age") int age,
        @FormParam("email") String email) {
}
