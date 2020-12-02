package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import java.util.List;

public interface ParameterSubResGenericInterface<T> {
   @GET
   String get(@QueryParam("foo") List<T> params);
}
