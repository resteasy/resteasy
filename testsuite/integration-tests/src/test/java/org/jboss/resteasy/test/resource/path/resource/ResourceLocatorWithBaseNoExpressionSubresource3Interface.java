package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;

public interface ResourceLocatorWithBaseNoExpressionSubresource3Interface {
   @GET
   @Path("3")
   String get(@QueryParam("foo") List<Double> params);
}
