package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("/")
public class HeaderParamParamConverterTestServiceImpl implements HeaderParamParamConverterTestService{
    @GET
    public boolean test(@HeaderParam("test-header") HeaderParamMyClass someValue){
        return "someValue-MORE".equals(someValue.toString());
    }
}
