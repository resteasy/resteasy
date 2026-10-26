package org.jboss.resteasy.test.cdi.modules.resource;

import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Returns an instance of a CDI bean class from {@link #getSingletons()}. The instance is not created by CDI, so
 * RESTEasy must inject its {@code @Context} members. The {@link #PROPERTY} property identifies this application.
 */
@ApplicationPath("/")
public class CDIModulesSingletonsApplication extends Application {
    public static final String PROPERTY = "application";
    public static final String PROPERTY_VALUE = "singletons";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(CDIModulesLibraryProviderResource.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Set<Object> getSingletons() {
        return Set.of(new CDIModulesLibraryProvider());
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of(PROPERTY, PROPERTY_VALUE);
    }
}
