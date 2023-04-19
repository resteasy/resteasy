package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface ResourceLocatorWithBaseExpressionSubresource3Interface {
    @GET
    @Path("3")
    String get(@QueryParam("foo") List<Double> params);
}
