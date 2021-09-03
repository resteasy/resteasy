package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

@Path("/")
public class HeaderParamParamConverterTestServiceImpl implements HeaderParamParamConverterTestService{
    @GET
    public boolean test(@HeaderParam("test-header") HeaderParamMyClass someValue){
        return "someValue-MORE".equals(someValue.toString());
    }
}
