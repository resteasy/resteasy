package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

public interface ResourceLocatorWithBaseNoExpressionSubresource3Interface {
    @GET
    @Path("3")
    String get(@QueryParam("foo") List<Double> params);
}
