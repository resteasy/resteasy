package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/repository/workspaces")
public class ComplexPathParamRegressionResteasy145 {
    @Path("{service: x.*}")
    public ComplexPathParamSubRes getService(@PathParam("service") String serviceName) {
        return new ComplexPathParamSubRes();
    }

    @Path("{path:.*}")
    public ComplexPathParamSubResSecond getChild(@PathParam("path") String path) {
        return new ComplexPathParamSubResSecond();
    }
}
