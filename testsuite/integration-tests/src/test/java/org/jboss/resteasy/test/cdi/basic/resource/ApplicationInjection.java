package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationPath("/")
@ApplicationScoped
public class ApplicationInjection extends Application {
    public static volatile List<ApplicationInjection> instances = new ArrayList<>();

    @Context
    private Application app;

    public ApplicationInjection() {
        instances.add(this);
    }

    public Application getApplication() {
        return app;
    }
}
