package org.jboss.resteasy.test.cdi.generic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface ConcreteResourceIntf {
    @GET
    @Path("injection")
    Response testGenerics();

    @GET
    @Path("decorators/clear")
    Response clear();

    @GET
    @Path("decorators/execute")
    Response execute();

    @GET
    @Path("decorators/test")
    Response testDecorators();
}