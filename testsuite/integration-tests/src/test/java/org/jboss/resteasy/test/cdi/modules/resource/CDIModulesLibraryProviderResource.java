package org.jboss.resteasy.test.cdi.modules.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;

@Path("/library-provider")
public class CDIModulesLibraryProviderResource {

    @Context
    private Application application;

    @GET
    public String get() {
        return "library-provider";
    }

    /**
     * Returns the {@link CDIModulesSingletonsApplication#PROPERTY} property of the injected {@link Application}.
     *
     * @return the property value, or {@code "null"} if the application does not define it
     */
    @GET
    @Path("application")
    public String application() {
        return String.valueOf(application.getProperties().get(CDIModulesSingletonsApplication.PROPERTY));
    }
}
