package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
@ApplicationPath("/")
public class ApplicationInjection extends Application {
    public static volatile List<ApplicationInjection> instances = new ArrayList<>();

    @Context
    public Application app;

    public ApplicationInjection() {
        instances.add(this);
    }
}
