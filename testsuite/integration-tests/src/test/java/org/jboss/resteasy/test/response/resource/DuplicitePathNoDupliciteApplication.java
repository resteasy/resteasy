package org.jboss.resteasy.test.response.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/f")
public class DuplicitePathNoDupliciteApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(DuplicitePathMethodResource.class);
        set.add(DuplicitePathDupliciteResourceOne.class);
        set.add(DuplicitePathDupliciteResourceTwo.class);
        return set;
    }
}
