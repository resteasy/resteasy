package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

public interface ResourceLocatorWithBaseNoExpressionSubresource3Interface {
    @GET
    @Path("3")
    String get(@QueryParam("foo") List<Double> params);
}
