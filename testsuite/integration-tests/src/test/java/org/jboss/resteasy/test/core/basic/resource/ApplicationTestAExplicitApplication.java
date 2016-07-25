package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("a/explicit")
public class ApplicationTestAExplicitApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(ApplicationTestResourceA.class);
        return set;
    }
}
