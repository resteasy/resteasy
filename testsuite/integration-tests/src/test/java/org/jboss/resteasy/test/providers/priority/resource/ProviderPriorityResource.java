package org.jboss.resteasy.test.providers.priority.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

@RequestScoped
@Path("")
public class ProviderPriorityResource {

    @Inject
    ProviderPriorityFooParamConverterProviderCCC paramConvertrProvider;
    @Inject
    ProviderPriorityExceptionMapperCCC exceptionMapper;

    @GET
    @Path("exception")
    public Response exception() throws Exception {
        throw new ProviderPriorityTestException();
    }

    @GET
    @Path("paramconverter/{foo}")
    public String paramConverter(@PathParam("foo") ProviderPriorityFoo foo) throws Exception {
        return foo.getFoo();
    }

    @GET
    @Path("register")
    public Response register() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        factory.registerProviderInstance(paramConvertrProvider);
        factory.registerProviderInstance(exceptionMapper);
        return Response.ok("ok").build();
    }
}
