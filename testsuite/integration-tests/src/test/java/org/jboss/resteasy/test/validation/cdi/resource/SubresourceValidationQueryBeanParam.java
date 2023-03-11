package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.QueryParam;

public class SubresourceValidationQueryBeanParam {
    @Size(max = 5)
    @QueryParam("limit")
    private String limit;

    String getLimit() {
        return limit;
    }
}
