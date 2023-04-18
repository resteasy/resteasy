package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("/")
public interface HeaderParamParamConverterTestService {
    @GET
    boolean test(@HeaderParam("test-header") HeaderParamMyClass someValue);
}
