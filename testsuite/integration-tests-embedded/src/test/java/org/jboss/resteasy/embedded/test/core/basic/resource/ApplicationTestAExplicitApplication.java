package org.jboss.resteasy.embedded.test.core.basic.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("a/explicit")
public class ApplicationTestAExplicitApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(ApplicationTestResourceA.class);
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet<>();
        set.add(new ApplicationTestSingletonA());
        return set;
    }
}
