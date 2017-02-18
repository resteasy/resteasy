package org.jboss.resteasy.resteasy1569;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class used from deployment.
 */
@Provider
@ApplicationPath("/")
public class TestApplication extends Application {

    /**
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ResourceLocatorRegexCapturingGroupSubResourceWithPath.class);
        classes.add(ResourceLocatorRegexCapturingGroupSubResourceNoPath.class);
        classes.add(ResourceLocatorRegexCapturingGroup.class);
        classes.add(ResourceLocatorRegexNonCapturingGroup.class);
        return classes;
    }
}