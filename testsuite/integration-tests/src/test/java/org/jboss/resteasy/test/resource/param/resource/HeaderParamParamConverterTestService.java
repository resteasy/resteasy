package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.HeaderParam;

@Path("/")
public interface HeaderParamParamConverterTestService {
    @GET
    boolean test(@HeaderParam("test-header") HeaderParamMyClass someValue);
}
