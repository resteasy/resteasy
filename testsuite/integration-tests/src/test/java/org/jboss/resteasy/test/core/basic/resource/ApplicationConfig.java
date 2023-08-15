package org.jboss.resteasy.test.core.basic.resource;

import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@ApplicationPath("/")
@ApplicationScoped
@FollowUpRequired("The @ApplicationScope annotation can be removed once @ApplicationPath is a bean defining annotation.")
public class ApplicationConfig extends Application {
    private static ApplicationConfig instance;

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    @Inject
    private UriInfo field;
    private UriInfo setter;
    private UriInfo constructor;

    @FollowUpRequired("This can be removed once RESTEasy no longer attempts to create resources.")
    public ApplicationConfig() {

    }

    @Inject
    public ApplicationConfig(final UriInfo uriInfo) {
        this.constructor = uriInfo;
        classes.add(ApplicationConfigResource.class);
        classes.add(ApplicationConfigService.class);
        classes.add(ApplicationConfigQuotedTextWriter.class);
        classes.add(ApplicationConfigInjectionResource.class);
        instance = this;
    }

    public static ApplicationConfig getInstance() {
        return instance;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Inject
    public void setSetter(UriInfo setter) {
        this.setter = setter;
    }

    public boolean isFieldInjected() {
        return field != null;
    }

    public boolean isSetterInjected() {
        return setter != null;
    }

    public boolean isConstructorInjected() {
        return constructor != null;
    }
}
