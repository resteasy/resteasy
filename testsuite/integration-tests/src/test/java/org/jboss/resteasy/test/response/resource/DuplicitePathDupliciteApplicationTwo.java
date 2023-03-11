package org.jboss.resteasy.test.response.resource;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("a")
public class DuplicitePathDupliciteApplicationTwo extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(DuplicitePathDupliciteResourceTwo.class);
        return set;
    }
}
