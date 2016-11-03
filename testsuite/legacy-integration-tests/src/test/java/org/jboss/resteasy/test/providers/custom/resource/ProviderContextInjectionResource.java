package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

@Path("resource")
public class ProviderContextInjectionResource {

    @Context
    Providers providers;

    @GET
    @Path("isRegisteredContextResolver")
    public Response isRegisteredContextResolver() {
        ProviderContextInjectionEnumProvider ep = getEnumProvider(MediaType.WILDCARD_TYPE);
        return getResponseByEnumProvider(ProviderContextInjectionEnumProvider.JAXRS, ep);
    }

    private ProviderContextInjectionEnumProvider getEnumProvider(MediaType type) {
        ContextResolver<ProviderContextInjectionEnumProvider> scr = providers.getContextResolver(
                ProviderContextInjectionEnumProvider.class, type);
        ProviderContextInjectionEnumProvider ep = scr.getContext(ProviderContextInjectionEnumProvider.class);
        return ep;
    }

    @GET
    @Path("isRegisteredRuntimeExceptionMapper")
    public Response isRegisteredRuntimeExceptionMapper() {
        ExceptionMapper<RuntimeException> em = providers
                .getExceptionMapper(RuntimeException.class);
        Response.Status status = Response.Status.NOT_ACCEPTABLE;
        if (em != null && em.getClass() == ProviderContextInjectionAnyExceptionExceptionMapper.class) {
            status = Response.Status.OK;
        }
        return Response.status(status).build();
    }

    Response getResponseByEnumProvider(ProviderContextInjectionEnumProvider expected, ProviderContextInjectionEnumProvider given) {
        Response.Status status = Response.Status.NO_CONTENT;
        if (given != null) {
            status = given != expected ? Response.Status.NOT_ACCEPTABLE : Response.Status.OK;
        }
        return Response.status(status).build();
    }
}
