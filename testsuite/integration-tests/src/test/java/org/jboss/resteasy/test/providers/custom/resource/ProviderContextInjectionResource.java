package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("resource")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class ProviderContextInjectionResource {

    @Inject
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
        if (ProviderContextInjectionAnyExceptionExceptionMapper.class.isInstance(em)) {
            status = Response.Status.OK;
        }
        return Response.status(status).build();
    }

    Response getResponseByEnumProvider(ProviderContextInjectionEnumProvider expected,
            ProviderContextInjectionEnumProvider given) {
        Response.Status status = Response.Status.NO_CONTENT;
        if (given != null) {
            status = given != expected ? Response.Status.NOT_ACCEPTABLE : Response.Status.OK;
        }
        return Response.status(status).build();
    }
}
