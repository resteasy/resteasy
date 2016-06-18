package org.jboss.resteasy.resteasy1405;

import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import javax.ws.rs.core.Application;
import java.util.Set;

public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(MyResource.class);
        return unmodifiableSet(classes);
    }

}
