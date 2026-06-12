package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

/**
 * Java Record for testing @BeanParam with primitive types.
 */
public record RecordPrimitivesParam(
        @FormParam("count") int count,
        @FormParam("active") boolean active,
        @FormParam("score") double score) {
}
