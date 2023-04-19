package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;

public class ComplexPathParamSubResSecond {
    @GET
    public String get() {
        return "sub2";
    }
}
