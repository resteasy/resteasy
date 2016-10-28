package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("a")
public class DuplicitePathDupliciteApplicationOne extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> set = new HashSet<Class<?>>();
        set.add(DuplicitePathDupliciteResourceOne.class);
        return set;
    }
}